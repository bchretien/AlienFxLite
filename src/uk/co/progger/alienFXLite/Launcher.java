package uk.co.progger.alienFXLite;

public class Launcher {
	public static void main(String[] args) {
        JarClassLoader jcl = new JarClassLoader();
        try {
            jcl.invokeMain(AlienFXProperties.ALIEN_FX_MAIN_CLASS, args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    } // main()
    
} // class Launcher
