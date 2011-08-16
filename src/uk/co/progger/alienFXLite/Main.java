package uk.co.progger.alienFXLite;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import uk.co.progger.alienFXLite.gui.AlienFXTexts;
import uk.co.progger.alienFXLite.gui.MainFrame;

public class Main {
	
	/**
	 * Boolean which holds if the start should be silent (e.g. the window be not visible by default). This is set by parseArguments
	 */
	private static boolean silentStart = false;
	
	/**
	 * Main entry into the application.
	 * @param args
	 */
	public static void main(String[] args) {
		parseArguments(args);
		//set default look and feel. Dont care if an error happens
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception e){System.err.println(AlienFXTexts.SYSTEM_UI_NOT_FOUND);};
		try { JarClassLoader.loadLookAndFeel();}catch(Exception e){};
		
		//launch the main frame
		SwingUtilities.invokeLater(new Runnable(){ public void run(){new MainFrame(silentStart);}});
	}
	
	
	/**
	 * Function to parse the command line arguments
	 * @param args - command line arguments
	 */
	private static void parseArguments(String args[]){
		for(String s : args){
			if(s.equals(AlienFXProperties.ALIEN_FX_SILENT_ARGUMENT))
				silentStart = true;
		}
	}
}
