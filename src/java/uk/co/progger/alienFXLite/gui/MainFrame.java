package uk.co.progger.alienFXLite.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import uk.co.progger.alienFXLite.AlienFXProperties;
import uk.co.progger.alienFXLite.alienfx.AlienFXEngine;
import uk.co.progger.alienFXLite.alienfx.AlienFXProfile;
import uk.co.progger.alienFXLite.alienfx.AlienFXProfiles;
import uk.co.progger.alienFXLite.led.AlienFXCommunicationException;
import uk.co.progger.alienFXLite.led.AlienFXControllerNotFoundException;
import uk.co.progger.alienFXLite.led.AlienFXControllerUnknownDeviceException;

public class MainFrame extends JFrame{
	
	private static final long serialVersionUID = 1L;
	private AlienFXEngine engine;
	
	private File lockFile;
	private FileChannel channel;
	private FileLock lock;
	
	private JMenuItem defaultItem;
	private JMenuItem showItem;
	private JPopupMenu systemTrayPopUp;
    private AlienFXProfiles profiles;
    private boolean useTrays;
    private JXTrayIcon trayIcon;
    private Insets insets;
    
	public MainFrame(boolean silent){
		super(AlienFXProperties.ALIEN_FX_APPLICATION_NAME);
		
		try{
			lockApplication();
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, AlienFXTexts.ALREADY_RUNNING_ERROR_TEXT, AlienFXTexts.ALIEN_FX_ERROR_TITLE_TEXT, JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		ProfileModel model = new ProfileModel();
		model.addObserver(new ProfileListener());
		try {
			engine = new AlienFXEngine();
			profiles = new AlienFXProfiles(model);
			profiles.loadProfiles();
			setupTray();
		} catch (AlienFXControllerNotFoundException e) {
			JOptionPane.showMessageDialog(null, AlienFXTexts.DEVICE_NOT_PRESENT_ERROR_TEXT, AlienFXTexts.ALIEN_FX_ERROR_TITLE_TEXT, JOptionPane.ERROR_MESSAGE);
			return;
		} catch (AlienFXControllerUnknownDeviceException e) {
			JOptionPane.showMessageDialog(null, AlienFXTexts.UNKNOWN_DEVICE_ERROR_TEXT, AlienFXTexts.ALIEN_FX_ERROR_TITLE_TEXT, JOptionPane.ERROR_MESSAGE);
			return;
		} catch (AlienFXCommunicationException e) {
			JOptionPane.showMessageDialog(null, AlienFXTexts.DEVICE_PERMISSION_ERROR_TEXT, AlienFXTexts.ALIEN_FX_ERROR_TITLE_TEXT, JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		
		JColorChooser chooser = new JColorChooser();
		chooser.setPreviewPanel(new JPanel());
		
		ColorModel colorModel = new ColorModel();

		// Top row: device name, profile selection panel
		JPanel toprow = new JPanel(new BorderLayout());
		toprow.add(new DeviceInfoPanel(engine.getController().description), BorderLayout.LINE_START);
		toprow.add(new ProfileSelectionPanel(model, engine, profiles), BorderLayout.CENTER);
		add(toprow, BorderLayout.PAGE_START);

		JPanel profile = new ProfilePanel(model,colorModel);
		profile.setBorder(BorderFactory.createTitledBorder(AlienFXTexts.PROFILE_TEXT));
		add(profile, BorderLayout.CENTER);

		JPanel leftcolumn = new JPanel(new BorderLayout());
		JPanel colorchooserpanel = new ColorChooserPanel(colorModel);
		
		colorchooserpanel.setBorder(BorderFactory.createTitledBorder(AlienFXTexts.COLORS_TEXT));
		leftcolumn.add(colorchooserpanel, BorderLayout.PAGE_START);
		leftcolumn.add(new ColorUsedPanel(model, colorModel), BorderLayout.CENTER);
		add(leftcolumn, BorderLayout.LINE_START);
		
		setSize(1100,550);
		setLocationRelativeTo(null);
		setResizable(true);
		addWindowListener(new CloseListener());
		setDefaultCloseOperation(useTrays ? JFrame.DO_NOTHING_ON_CLOSE : JFrame.EXIT_ON_CLOSE);
		setupMenu();
		setVisible((useTrays && !silent) || !useTrays);
		updateTray();
	}
	
	private void setupMenu(){
		JMenuBar menuBar = new JMenuBar();
		// File menu
		JMenu file_menu = new JMenu("File");

		JMenuItem profiles_folder = new JMenuItem("Open profiles folder");
		JMenuItem exit = new JMenuItem("Exit");

		profiles_folder.addActionListener(new OpenProfileFolderHandler());
		exit.addActionListener(new ExitListener());

		file_menu.add(profiles_folder);
		file_menu.add(exit);

		// Help menu
		JMenu help_menu = new JMenu("Help");

		JMenuItem usage = new JMenuItem("Usage");
		JMenuItem reset = new JMenuItem("Reset AlienFX");
		JMenuItem about = new JMenuItem("About");

		usage.addActionListener(new UsageHandler());
		reset.addActionListener(new ResetHandler());
		about.addActionListener(new AboutHandler());

		help_menu.add(usage);
		help_menu.add(reset);
		help_menu.add(about);

		// Add menus to menu bar
		menuBar.add(file_menu);
		menuBar.add(help_menu);

		setJMenuBar(menuBar);

	}
	
	private void setupTray(){
		try{
			SystemTray tray = SystemTray.getSystemTray();
			Image image = createImage();
			systemTrayPopUp = new JPopupMenu();
			
			defaultItem = new JMenuItem(AlienFXTexts.EXIT_TEXT);
			insets = new Insets(0, 20, 5, 20);
			
			defaultItem.setPreferredSize(new Dimension(150,20));
			defaultItem.setMargin(insets);
			defaultItem.addActionListener(new ExitListener());
	
			showItem = new JMenuItem(AlienFXTexts.SHOW_ALIEN_FX_LITE_TEXT); 
			showItem.addActionListener(new ShowAction());
			showItem.setMargin(insets);
			
			trayIcon = new JXTrayIcon(image);
		    trayIcon.setImageAutoSize(true);
		    trayIcon.setJPopupMenu(systemTrayPopUp);
		    trayIcon.setToolTip("AlienFxLite");
		    
		    tray.add(trayIcon);
		    useTrays = true;
		    profiles.addListDataListener(new ChangeListener());
		}catch(Exception e){
			JOptionPane.showMessageDialog(this.getRootPane(), AlienFXTexts.SYSTEM_TRAY_WARNING_TEXT, AlienFXTexts.ALIEN_FX_WARNING_TITLE_TEXT, JOptionPane.INFORMATION_MESSAGE);
			useTrays = false;
		}	
	}
	
	private Image createImage() {
		URL alienURL = this.getClass().getResource(AlienFXResources.ALIENFX_ICON_NAME);
		ImageIcon ic = new ImageIcon(alienURL);
		Image image = ic.getImage();
		if(AlienFXProperties.isWindows)
			return image;
		
		BufferedImage i = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) i.getGraphics();
        g2.setColor(getBackground());
        g2.fillRect(0, 0, 32, 32);
        g2.drawImage(image, 0, 0,32,32, null);
        g2.dispose();
		return i;
	}

	private void updateTray(){
		systemTrayPopUp.removeAll();
		
		for(AlienFXProfile p : profiles.getProfiles()){
			JMenuItem item = new JMenuItem(p.getName());
			item.setMargin(insets);
			item.addActionListener(new Action(p));
			systemTrayPopUp.add(item);
		}
		if(!isVisible()){
			systemTrayPopUp.addSeparator();
			systemTrayPopUp.add(showItem);
		}
		systemTrayPopUp.addSeparator();
		systemTrayPopUp.add(defaultItem);
	}
	
	//closes the application gracefully
	public void shutDown(){
		engine.shutDown();
		unlockApplication();
		System.exit(0);
	}
	
	private void lockApplication() throws IOException, AlienFXAlreadyRunningException{
		lockFile = new File(AlienFXProperties.ALIEN_FX_LOCK_FILE_PATH);
		// Try to get the lock
		channel = new RandomAccessFile(lockFile, "rw").getChannel();
		lock = channel.tryLock();
		if(lock == null){
			channel.close();
			throw new AlienFXAlreadyRunningException();
		}
	}
	
	private void unlockApplication(){
		try {
			if(lock != null) {
				lock.release();
				channel.close();
				lockFile.delete();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private class ProfileListener implements Observer{
		public void update(Observable arg0, Object arg1) {
			//setSize(getPreferredSize());
		}		
	}

	private class OpenProfileFolderHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				Desktop.getDesktop().open(new File(AlienFXProperties.ALIEN_FX_PROFILE_FOLDER_PATH));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	private class AboutHandler implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(getRootPane(), String.format(AlienFXTexts.ABOUT_FORMAT, AlienFXProperties.ALIEN_FX_VERSION, AlienFXProperties.AUTHOR), AlienFXTexts.ABOUT_TITLE, JOptionPane.INFORMATION_MESSAGE, null);
		}		
	}
	
	private class ResetHandler implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			try {
				engine.reset();
			} catch (AlienFXCommunicationException e1) {
				JOptionPane.showMessageDialog(getRootPane(), String.format(AlienFXTexts.COMMUNICATION_ERROR_FORMAT,e1.getMessage()), AlienFXTexts.ALIEN_FX_ERROR_TITLE_TEXT, JOptionPane.ERROR_MESSAGE);
			}
		}		
	}
	
	private class UsageHandler implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			// Information panel with HTML text
			JEditorPane editorPane = new JEditorPane();
			editorPane.setEditable(false);
			editorPane.setContentType("text/html");
			editorPane.setText(AlienFXTexts.USAGE);

			// Scrollable panel
			JScrollPane editorScrollPane = new JScrollPane(editorPane);
			editorScrollPane.setVerticalScrollBarPolicy(
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			editorScrollPane.setPreferredSize(new Dimension(450, 400));
			editorScrollPane.setMinimumSize(new Dimension(10, 10));

			// Display the panel
			JOptionPane.showMessageDialog(getRootPane(), editorScrollPane,
					AlienFXTexts.USAGE_TITLE, JOptionPane.INFORMATION_MESSAGE,
					null);
		}		
	}
	
	private class ChangeListener implements ListDataListener{
		public void contentsChanged(ListDataEvent e) {updateTray();}
		public void intervalAdded(ListDataEvent e) {updateTray();}
		public void intervalRemoved(ListDataEvent e) {updateTray();}
	}
	
	private class Action implements ActionListener{
		private AlienFXProfile pf;
		public Action(AlienFXProfile pf) { super(); this.pf = pf; }
		public void actionPerformed(ActionEvent e) {
			try {
				engine.applyProfile(pf);
			} catch (AlienFXCommunicationException e1) {
				MainFrame.this.trayIcon.displayMessage( AlienFXTexts.ALIEN_FX_ERROR_TITLE_TEXT, String.format(AlienFXTexts.COMMUNICATION_ERROR_FORMAT, e1.getMessage()), TrayIcon.MessageType.ERROR);
			}
		}		
	}
	
	private class ShowAction implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			MainFrame.this.setVisible(true);
			updateTray();
		}		
	}
	
	private class ExitListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			int choice = JOptionPane.showConfirmDialog(null,
					"Are you sure you want to exit AlienFXLite?", "Exit",
					JOptionPane.YES_NO_OPTION);
			if (choice == JOptionPane.YES_OPTION)
				shutDown();
		}		
	}
	
	private class CloseListener implements WindowListener{
		public void windowActivated(WindowEvent arg0) {}
		public void windowClosed(WindowEvent arg0) {}

		public void windowClosing(WindowEvent arg0) {
			if (useTrays) {
				// MainFrame.this.trayIcon.displayMessage(
				// AlienFXTexts.ALIEN_FX_INFO_TITLE_TEXT,
				// AlienFXTexts.ALIEN_FX_BACKGROUND_TEXT,
				// TrayIcon.MessageType.INFO);
				MainFrame.this.setVisible(false);
				updateTray();
			}
		}
		public void windowDeactivated(WindowEvent arg0) {}
		public void windowDeiconified(WindowEvent arg0) {}
		public void windowIconified(WindowEvent arg0) {}
		public void windowOpened(WindowEvent arg0) {}
	}
}
