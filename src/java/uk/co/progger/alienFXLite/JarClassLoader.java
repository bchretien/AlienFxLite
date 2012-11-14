/*
 * File: JarClassLoader.java
 * 
 * Copyright (C) 2008 JDotSoft. All Rights Reserved.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301  USA
 * 
 * Visit jdotsoft.com for commercial license.
 * 
 * $Id: JarClassLoader.java,v 1.22 2009/08/22 14:44:25 mg Exp $
 */
package uk.co.progger.alienFXLite;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 * This classloader loads classes, native libraries and resources from 
 * the top JAR and from JARs inside top JAR. The loading process looks 
 * through JARs hierarchy and allows their tree structure, i.e. nested JARs.
 * <p>
 * This class delegates class loading to the parent class loader and 
 * successfully loads classes, native libraries and resources when it works 
 * not in a JAR environment. 
 * <p>
 * Create a <code>Launcher</code> class to use this class loader 
 * and start its main() method to start your application
 * <code>com.mycompany.MyApp</code>
 * <code> 
<pre>
public class Launcher {

    public static void main(String[] args) {
        JarClassLoader jcl = new JarClassLoader();
        System.out.println("Starting TestMain...");
        try {
            jcl.invokeMain("com.mycompany.MyApp", args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    } // main()
    
} // class Launcher
</pre>
 * </code>
 * <p>
 * An application could be started from a command line using its main 
 * class e.g. <code>TestMain.main()</code> or from <code>Launcher.main()</code> 
 * similar to the the above example. The application behavior in both cases 
 * is identical if resources are loaded from a file system.  
 * Starting from <code>Launcher.main()</code> is required only to start
 * the application from a JAR file which contains other JARs or native libraries.
 * <p>
 * Special handling is required for loading external LaF classes. 
 * Call the method <code>JarClassLoader.loadLookAndFeel();</code> to preload
 * UI classes.
 * <p>
 * Known issues: temporary files with loaded native libraries are not deleted on
 * application exit because JVM does not close handles to them. The loader
 * attempts to delete them on next launch. The list of these temporary files
 * is preserved in the "[user.home]/.JarClassLoader" file.
 * <p>
 * See also discussion "How load library from jar file?" 
 * http://discuss.develop.com/archives/wa.exe?A2=ind0302&L=advanced-java&D=0&P=4549
 * Unfortunately, the native method java.lang.ClassLoader$NativeLibrary.unload()
 * is package accessed in a package accessed inner class. 
 * Moreover, it's called from finalizer. This does not allow releasing
 * the native library handle and delete the temporary library file.
 * Option to explore: use JNI function UnregisterNatives(). See also
 * native code in ...\jdk\src\share\native\java\lang\ClassLoader.c 
 *  
 * @version $Revision: 1.22 $
 */
public class JarClassLoader extends ClassLoader {

    /**
     * VM parameter to turn on debugging logging to file or console.
     * <br>
     * Specify <code>-DJarClassLoader.logger=[filename]</code> in the 
     * command line for logging on into the file or to console if the 
     * filename is specified as a "console".
     */
    public static final String KEY_LOGGER = "JarClassLoader.logger";
    public static final String CONSOLE = "console";
    
    private PrintStream logger;
    private List<JarFile> lstJarFile;
    private Set<File> hsNativeFile;
    private Map<String, Class<?>> hmClass;
    private ProtectionDomain pd;

    /**
     * Default constructor. 
     * Defines system class loader as a parent class loader.
     */
    public JarClassLoader() {
        this(ClassLoader.getSystemClassLoader());
    }

    /**
     * Constructor
     * 
     * @param parent class loader parent
     */
    public JarClassLoader(ClassLoader parent) {
        super(parent);
        String sLogger = System.getProperty(KEY_LOGGER);
        if (sLogger != null) {
            if (sLogger.equals(CONSOLE)) {
                this.logger = System.out;  
            } else {
                try {
                    this.logger = new PrintStream(sLogger);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(
                            "JarClassLoader: cannot create log file: " + e);
                }
            }
        }
        hmClass = new HashMap<String, Class<?>>();
        lstJarFile = new ArrayList<JarFile>();
        hsNativeFile = new HashSet<File>();
        
        String sUrlTopJAR = null;
        try {
            pd = getClass().getProtectionDomain();
            CodeSource cs = pd.getCodeSource();
            URL urlTopJAR = cs.getLocation();
            // URL.getFile() returns "/C:/my%20dir/MyApp.jar"
            sUrlTopJAR = URLDecoder.decode(urlTopJAR.getFile(), "UTF-8");
            log("Loading from top JAR: %s", sUrlTopJAR);
            loadJar(new JarFile(sUrlTopJAR)); // throws if not JAR
        } catch (IOException e) {
            // Expected exception: loading NOT from JAR.
            log("Not a JAR: %s %s", sUrlTopJAR, e.toString());
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                shutdown();
            }
        });
        UIManager.addPropertyChangeListener(new PropertyChangeListener() {
            // @Override - commented out to comply with Java 1.5
            public void propertyChange(PropertyChangeEvent evt) {
                if ("lookAndFeel".equals(evt.getPropertyName())) {
                    try {
                        loadLookAndFeel();
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(
                                "Failure to load LaF " + evt.getNewValue(), e);
                    }
                }
            }
        });
    } // JarClassLoader()
    
    //--------------------------------separator--------------------------------
    static int ______INIT;

    /**
     * Using temp files (one per inner JAR/DLL) solves many issues:
     * 1. There are no ways to load JAR defined in a JarEntry directly
     *    into the JarFile object.
     * 2. Cannot use memory-mapped files because they are using
     *    nio channels, which are not supported by JarFile ctor.
     * 3. JarFile object keeps opened JAR files handlers for fast access.
     * 4. Resource in a jar-in-jar does not have well defined URL.
     *    Making temp file with JAR solves this problem.
     * 5. Similar issues with native libraries: 
     *    <code>ClassLoader.findLibrary()</code> accepts ONLY string with 
     *    absolute path to the file with native library.
     * 
     * @param inf JAR entry information
     * @return temporary file object presenting JAR entry 
     * @throws JarClassLoaderException
     */
    private File createTempFile(JarEntryInfo inf) 
    throws JarClassLoaderException {
        byte[] a_by = getJarBytes(inf);
        try {
            File file = File.createTempFile(inf.getName() + ".", null);
            file.deleteOnExit();
            BufferedOutputStream os = new BufferedOutputStream( 
                                      new FileOutputStream(file));
            os.write(a_by);
            os.close();
            return file;
        } catch (IOException e) {
            throw new JarClassLoaderException("Cannot create temp file for " + inf.jarEntry, e);
        }
    } // createTempFile()    
    
    /**
     * Loads specified JAR
     * 
     * @param jarFile JAR file
     */
    private void loadJar(JarFile jarFile) {
        lstJarFile.add(jarFile);
        try {
            Enumeration<JarEntry> en = jarFile.entries();
            final String EXT_JAR = ".jar";
            while (en.hasMoreElements()) {
                JarEntry je = en.nextElement();
                if (je.isDirectory()) {
                    continue;
                }
                String s = je.getName().toLowerCase(); // JarEntry name
                if (s.lastIndexOf(EXT_JAR) == s.length() - EXT_JAR.length()) {
                    JarEntryInfo inf = new JarEntryInfo(jarFile, je); 
                    File file = createTempFile(inf);
                    log("Loading inner JAR: %s from temp file %s", 
                            inf.jarEntry, getFilename4Log(file));
                    try {
                        loadJar(new JarFile(file));
                    } catch (IOException e) {
                        throw new JarClassLoaderException("Cannot load inner JAR " + inf.jarEntry, e);
                    }
                }
            }
        } catch (JarClassLoaderException e) {
            throw new RuntimeException(
                    "ERROR on loading InnerJAR: " + e.getMessageAll());
        }
    } // loadJar()
    
    private JarEntryInfo findJarEntry(String sName) {
        for (JarFile jarFile : lstJarFile) {
            JarEntry jarEntry = jarFile.getJarEntry(sName);
            if (jarEntry != null) {
                return new JarEntryInfo(jarFile, jarEntry);
            }
        }
        return null;
    } // findJarEntry()
    
    private List<JarEntryInfo> findJarEntries(String sName) {
        List<JarEntryInfo> lst = new ArrayList<JarEntryInfo>();
        for (JarFile jarFile : lstJarFile) {
            JarEntry jarEntry = jarFile.getJarEntry(sName);
            if (jarEntry != null) {
                lst.add(new JarEntryInfo(jarFile, jarEntry));
            }
        }
        return lst;
    } // findJarEntries()
    
    /**
     * Finds native library entry.
     * 
     * @param sLib Library name. For example for the name "Native"
     * the Windows system returns entry for "Native.dll",
     * the Linux system returns entry for "libNative.so".
     * The path to the entry is ignored, i.e the library could be in 
     * any location in the JAR: "lib/Native.dll" or "bin/Native.dll" or any.
     * @return Native library entry
     */
    private JarEntryInfo findJarNativeEntry(String sLib) {
        String sName = System.mapLibraryName(sLib);
        for (JarFile jarFile : lstJarFile) {
            Enumeration<JarEntry> en = jarFile.entries();
            while (en.hasMoreElements()) {
                JarEntry je = en.nextElement();
                if (je.isDirectory()) {
                    continue;
                }
                // Example: sName is "Native.dll"
                String sEntry = je.getName(); // "lib/Native.dll"
                if (sEntry.lastIndexOf(sName) == sEntry.length() - sName.length()) {
                    return new JarEntryInfo(jarFile, je); 
                }
            }
        }
        return null;
    } // findJarNativeEntry()
    
    /**
     * Loads class from a JAR and searches for all jar-in-jar.
     *  
     * @param sClassName class to load
     * @return loaded class
     * @throws JarClassLoaderException
     */
    private Class<?> findJarClass(String sClassName) throws JarClassLoaderException {
        // http://java.sun.com/developer/onlineTraining/Security/Fundamentals
        //       /magercises/ClassLoader/solution/FileClassLoader.java        
        Class<?> c = hmClass.get(sClassName);
        if (c != null) {
            return c;
        }
        // Char '/' works for Win32 and Unix.
        String sName = sClassName.replace('.', '/') + ".class";
        JarEntryInfo inf = findJarEntry(sName);
        if (inf != null) {
            byte[] a_by = getJarBytes(inf);        
            try {
                c = defineClass(sClassName, a_by, 0, a_by.length, pd);
            } catch (ClassFormatError e) {
                throw new JarClassLoaderException(null, e);
            }
        }
        if (c == null) {
            throw new JarClassLoaderException(sClassName);
        }
        hmClass.put(sClassName, c);
        return c;        
    } // findJarClass()
    
    //--------------------------------separator--------------------------------
    static int ______SHUTDOWN;

    /**
     * Called on shutdown for temporary files cleanup
     */
    private void shutdown() {
        // All inner JAR temporary files are marked at the time of creation  
        // as deleteOnExit(). These files are not deleted if they are not closed.
        for (JarFile jarFile : lstJarFile) {
            try {
                jarFile.close();
            } catch (IOException e) {
                // Ignore. In the worst case temp files will accumulate.
            }
        }
        // JVM does not close handles to native libraries files 
        // and temp files even marked closeOnExit() are not deleted. 
        // Use special file with list of native libraries temp files
        // to delete them on next application run.
        String sPersistentFile = System.getProperty("user.home") 
                               + File.separator + ".JarClassLoader";
        deleteOldNative(sPersistentFile);
        persistNewNative(sPersistentFile);
    } // shutdown()
    
    /**
     * Deletes temporary files listed in the file.
     * The method is called on shutdown().
     * 
     * @param sPersistentFile file name with temporary files list 
     */
    private void deleteOldNative(String sPersistentFile) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(sPersistentFile));
            String sLine;
            while ((sLine = reader.readLine()) != null) {
                File file = new File(sLine);
                if (!file.exists()) {
                    continue; // already deleted; from command line?
                }
                if (!file.delete()) {
                    // Cannot delete, will try next time.
                    hsNativeFile.add(file);
                }
            }
        } catch (IOException e) {
            // Ignore. In the worst case temp files will accumulate.
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException e) { }
            }
        }
    } // deleteOldNative()
    
    /**
     * Creates file with temporary files list. This list will be used to 
     * delete temporary files on the next application launch.
     * The method is called from shutdown().
     * 
     * @param sPersistentFile file name with temporary files list 
     */
    private void persistNewNative(String sPersistentFile) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(sPersistentFile));
            for (File fileNative : hsNativeFile) {
                writer.write(fileNative.getCanonicalPath());
                writer.newLine();
                
                // The temporary file with native library is marked 
                // as deleteOnExit() but VM does not close it and it remains open.
                // Attempt to explicitly delete the file fails with "false"
                // because VM does not release the file handle.
                fileNative.delete(); // returns "false"
            }
        } catch (IOException e) {
            // Ignore. In the worst case temp files will accumulate.
        } finally {
            if (writer != null) {
                try { writer.close(); } catch (IOException e) { }
            }
        }
    } // persistNewNative()
    
    //--------------------------------separator--------------------------------
    static int ______ACCESS;

    /**
     * Checks how the application was loaded: from JAR or file system.
     * 
     * @return true if application was started from JAR
     */
    public boolean isLaunchedFromJar() {
        return (lstJarFile.size() > 0);
    } // isLaunchedFromJar()
    
    /**
     * Returns the name of the jar file main class, or null if
     * no "Main-Class" manifest attributes was defined.
     * 
     * @return main class declared in JAR's manifest
     */
    public String getManifestMainClass() {
        Attributes attr = null;
        if (isLaunchedFromJar()) {
            try {
                // The first element in array is the top level JAR
                Manifest m = lstJarFile.get(0).getManifest();
                attr = m.getMainAttributes();
            } catch (IOException e) {
            }
        }
        return (attr == null ? null : attr.getValue(Attributes.Name.MAIN_CLASS));
    }
    
    /**
     * Invokes main() method on class with provided parameters.
     * 
     * @param sClass class name in form "MyClass" for default package 
     * or "com.abc.MyClass" for class in some package
     * 
     * @param args arguments for the main() method or null
     * 
     * @throws Throwable wrapper for many exceptions thrown while 
     * <p>(1) main() method lookup: 
     *        ClassNotFoundException, SecurityException, NoSuchMethodException
     * <p>(2) main() method launch: 
     *        IllegalArgumentException, IllegalAccessException (disabled)
     * <p>(3) Actual cause of InvocationTargetException
     * 
     * See 
     * {@link http://java.sun.com/developer/Books/javaprogramming/JAR/api/jarclassloader.html} 
     * and 
     * {@link http://java.sun.com/developer/Books/javaprogramming/JAR/api/example-1dot2/JarClassLoader.java}
     */
    public void invokeMain(String sClass, String[] args) throws Throwable {
        Class<?> clazz = loadClass(sClass);
        log("Launch: %s.main(); Loader: %s", sClass, clazz.getClassLoader());
        Method method = clazz.getMethod("main", new Class<?>[] { String[].class });
        
        boolean bValidModifiers = false;
        boolean bValidVoid = false;
        
        if (method != null) {
            method.setAccessible(true); // Disable IllegalAccessException
            int nModifiers = method.getModifiers(); // main() must be "public static"
            bValidModifiers = Modifier.isPublic(nModifiers) && 
                             Modifier.isStatic(nModifiers);
            
            Class<?> clazzRet = method.getReturnType(); // main() must be "void"
            bValidVoid = (clazzRet == void.class); 
        }
        if (method == null  ||  !bValidModifiers  ||  !bValidVoid) {
            throw new NoSuchMethodException(
                    "The main() method in class \"" + sClass + "\" not found.");
        }
        
        // Invoke method.
        // Crazy cast "(Object)args" because param is: "Object... args"
        try {
            method.invoke(null, (Object)args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    } // invokeMain()
    
    //--------------------------------separator--------------------------------
    static int ______OVERRIDE;

    /**
     * ClassLoader JavaDoc encourages overriding findClass(String) in derived 
     * class rather than overriding this method. This does not work for 
     * loading classes from a JAR. Default implementation of loadClass() is 
     * able to load a class from a JAR without calling findClass().
     * This will "infect" the loaded class with a system class loader.  
     * The system class loader will be used to load all dependent classes  
     * and will fail for jar-in-jar classes. 
     * 
     * See also:
     * http://www.cs.purdue.edu/homes/jv/smc/pubs/liang-oopsla98.pdf
     */
    @Override
    protected synchronized Class<?> loadClass(String sClassName, boolean bResolve)
    throws ClassNotFoundException
    {
        log("Loading: %s (resolve=%b)", sClassName, bResolve);
        Class<?> c = null;
        try {
            // Step 1. Load from JAR.
            if (isLaunchedFromJar()) {
                try {
                    c = findJarClass(sClassName);
                    log("Loaded %s from JAR by %s", sClassName, getClass().getName());
                    return c;
                } catch (JarClassLoaderException e) {
                    if (e.getCause() == null) {
                        log("Not found %s in JAR by %s", 
                                e.getMessage(), getClass().getName());
                    } else {
                        log("Error %s in JAR by %s", e.getCause(), getClass().getName());
                    }
                    // keep looking
                }
            }
            // Step 2. Load by parent (usually system) class loader.
            // Call findSystemClass() AFTER attempt to find in a JAR. 
            // If it called BEFORE it will load class-in-jar using 
            // SystemClassLoader and "infect" it with SystemClassLoader.
            // The SystemClassLoader will be used to load all dependent 
            // classes. SystemClassLoader will fail to load a class from 
            // jar-in-jar and to load dll-in-jar. 
            try {
                // No need to call findLoadedClass(sClassName)  
                // because it's called inside:
                ClassLoader cl = getParent(); 
                c = cl.loadClass(sClassName);
                log("Loaded %s by %s", sClassName, cl.getClass().getName());
                return c;
            } catch (ClassNotFoundException e) {
                // keep looking
            }
            // What else?
            throw new ClassNotFoundException("Failure to load: " + sClassName);
        } finally {
            if (c != null  &&  bResolve) {
                resolveClass(c);
            }
        }
    } // loadClass()
    
    /** 
     * @see java.lang.ClassLoader#getResource(java.lang.String)
     */
    @Override
    public URL getResource(String sName) {
        if (isLaunchedFromJar()) {
            JarEntryInfo inf = findJarEntry(sName);
            return inf == null ? null : inf.getURL();
        }
        return getParent().getResource(sName);
    } // getResource()
    
    /**
     * @see java.lang.ClassLoader#getResources(java.lang.String)
     */
    @Override
    public Enumeration<URL> getResources(String sName) throws IOException {
        if (isLaunchedFromJar()) {
            List<JarEntryInfo> lstJarEntry = findJarEntries(sName);
            List<URL> lstURL = new ArrayList<URL>();
            for (JarEntryInfo inf : lstJarEntry) {
                URL url = inf.getURL();
                if (url != null) {
                    lstURL.add(url);
                }
            }
            return Collections.enumeration(lstURL);
        }
        return getParent().getResources(sName);
    } // getResources()

    /**
     * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
     */
    @Override
    public InputStream getResourceAsStream(String sName) {
        if (isLaunchedFromJar()) {
            JarEntryInfo inf = findJarEntry(sName);
            if (inf != null) {
                try {
                    return inf.jarFile.getInputStream(inf.jarEntry);
                } catch (IOException e) {
                }
            }
            return null;
        }
        return getParent().getResourceAsStream(sName);
    } // getResourceAsStream()

    /**
     * @see java.lang.ClassLoader#findLibrary(java.lang.String)
     */
    @Override
    protected String findLibrary(String sLib) {
        JarEntryInfo inf = findJarNativeEntry(sLib);
        if (inf != null) {
            try {
                File fileNative = createTempFile(inf); 
                log("Loading native library: %s from temp file %s", 
                        inf.jarEntry, getFilename4Log(fileNative));
                hsNativeFile.add(fileNative);
                return fileNative.getAbsolutePath();
            } catch (JarClassLoaderException e) {
                log("Failure to load native library %s: %s", sLib, e.toString());
            } 
        }
        return null;
    } // findLibrary()
    
    //--------------------------------separator--------------------------------
    static int ______HELPERS;

    /**
     * Read JAR entry and returns byte array of this JAR entry. This is
     * a helper method to load JAR entry into temporary file. 
     * 
     * @param inf JAR entry information object
     * @return byte array for the specified JAR entry
     * @throws JarClassLoaderException
     */
    private static byte[] getJarBytes(JarEntryInfo inf) 
    throws JarClassLoaderException 
    {
        DataInputStream dis = null;
        byte[] a_by = null;
        try {
            long lSize = inf.jarEntry.getSize(); 
            if (lSize <= 0  ||  lSize >= Integer.MAX_VALUE) {
                throw new JarClassLoaderException(
                        "Invalid size " + lSize + " for entry " + inf.jarEntry);
            }
            a_by = new byte[(int)lSize];
            InputStream is = inf.jarFile.getInputStream(inf.jarEntry);
            dis = new DataInputStream(is);
            dis.readFully(a_by);
        } catch (IOException e) {
            throw new JarClassLoaderException(null, e);
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                }
            }
        }
        return a_by;
    } // getJarBytes()

    /**
     * Call this method after UIManager.setLookAndFeel(..) to preload
     * non standard UI classes.
     * <br>
     * At runtime some JComponent class tries to load LaF UI class. 
     * The JVM uses the JComponent's class loader, which is system class loader 
     * to load UI class and fails because LaF classes reside in an external JAR.
     * A workaround is to preload LaF classes explicitly.  
     * <br>
     * See details
     * https://lists.xcf.berkeley.edu/lists/advanced-java/2001-January/015374.html
     * @throws ClassNotFoundException 
     */
    public static void loadLookAndFeel() throws ClassNotFoundException {
        LookAndFeel laf = UIManager.getLookAndFeel();
        if (laf == null) {
            return; // never null
        }
        ClassLoader cl = laf.getClass().getClassLoader();
        if (cl == null) {
            return; // null for system class loader (?)
        }
        
        // Does not work: cl.getClass().equals(JarClassLoader.class)
        if (cl.getClass().getName().equals(JarClassLoader.class.getName())) {
            UIDefaults uidef = UIManager.getDefaults();
            Enumeration<?> en = uidef.keys();
            while (en.hasMoreElements()) {
                String sClass = (String)en.nextElement();
                if (sClass.endsWith("UI")) {
                    Object obj = uidef.get(sClass);
                    // If the obj is java.lang.String load the class,
                    // otherwise it's already loaded java.lang.Class
                    if (obj instanceof String) {
                        Class<?> clazz = cl.loadClass((String)obj);
                        uidef.put(clazz.getName(), clazz);
                    }
                }
            }
        }
    } // loadLookAndFeel()

    private String getFilename4Log(File file) {
        if (logger != null) {
            try {
                // In form "C:\Documents and Settings\..."
                return file.getCanonicalPath();
            } catch (IOException e) {
                // In form "C:\DOCUME~1\..."
                return file.getAbsolutePath();
            }
        }
        return null;
    } // getFilename4Log()
    
    private void log(String sMsg, Object ... obj) {
        if (logger != null) {
            logger.printf("JarClassLoader: " + sMsg + "\n", obj);
        }
    } // log()

    /**
     * Inner class with JAR entry information. Keeps JAR file and entry object.
     */
    private static class JarEntryInfo {
        JarFile jarFile;
        JarEntry jarEntry;
        JarEntryInfo(JarFile jarFile, JarEntry jarEntry) {
            this.jarFile = jarFile;
            this.jarEntry = jarEntry;
        }
        URL getURL() {
            try {
                return new URL("jar:file:" + jarFile.getName() + "!/" + jarEntry);
            } catch (MalformedURLException e) {
                return null;
            }
        }
        String getName() {
            return jarEntry.getName().replace('/', '_');
        }
        @Override
        public String toString() {
            return "JAR: " + jarFile.getName() + " ENTRY: " + jarEntry;
        }
    } // inner class JarEntryInfo
    
    /**
     * Inner class to handle specific for the JarClassLoader exceptions  
     */
    private static class JarClassLoaderException extends Exception {
		private static final long serialVersionUID = 1L;
		JarClassLoaderException(String sMsg) {
            super(sMsg);
        }
        JarClassLoaderException(String sMsg, Throwable eCause) {
            super(sMsg, eCause);
        }
        String getMessageAll() {
            StringBuilder sb = new StringBuilder();
            for (Throwable e = this;  e != null;  e = e.getCause()) {
                if (sb.length() > 0) {
                    sb.append(" / ");
                }
                String sMsg = e.getMessage();
                if (sMsg == null  ||  sMsg.length() == 0) {
                    sMsg = e.getClass().getSimpleName();
                }
                sb.append(sMsg);
            }
            return sb.toString(); 
        }
    } // inner class JarClassLoaderException
    
} // class JarClassLoader
