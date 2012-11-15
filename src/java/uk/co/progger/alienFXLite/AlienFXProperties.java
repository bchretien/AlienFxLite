package uk.co.progger.alienFXLite;

import java.io.File;

public class AlienFXProperties {
	
	/**
	 * Defines whether the application is in debug mode or not
	 */
	public static final boolean isDebug = false;
	
	public static final String AUTHOR = "Filip Wieladek";
	
	//Application info
	public static final String ALIEN_FX_VERSION = "0.3";
	public static final String ALIEN_FX_APPLICATION_RAW_NAME = "AlienFX Lite";
	public static final String ALIEN_FX_APPLICATION_NAME = ALIEN_FX_APPLICATION_RAW_NAME +" "+ ALIEN_FX_VERSION;
	
	//java properties
	public static final String PROPERTY_OS_NAME = "os.name";
	public static final String USER_HOME = "user.home";
	public static final String JAVA_ARCHITECTURE = "sun.arch.data.model";
	
	//used properties
	public static final String arch = System.getProperty(JAVA_ARCHITECTURE);
	public static final String userHomePath = System.getProperty(USER_HOME)+File.separator;
	public static final String osName = System.getProperty(PROPERTY_OS_NAME);
	
	//OS checks
	public static final String WINDOWS_OS = "Windows";
	public static final boolean isWindows = AlienFXProperties.osName.contains(AlienFXProperties.WINDOWS_OS);

	//application configuration	
	public static final String ALIEN_FX_MAIN_CLASS = "uk.co.progger.alienFXLite.Main";
	public static final String ALIEN_FX_SILENT_ARGUMENT = "-s";
	public static final String ALIEN_FX_PROFILE_FOLDER_NAME = ".alienFXprofiles";
	public static final String ALIEN_FX_PROFILE_FOLDER_PATH = userHomePath+ALIEN_FX_PROFILE_FOLDER_NAME+File.separator;
	public static final String ALIEN_FX_PROFILE_EXTENSION = ".prof";
	
	//application locking
	public static final String ALIEN_FX_LOCK_FILE_NAME = ".alienFXLock";
	public static final String ALIEN_FX_LOCK_FILE_PATH = userHomePath+ALIEN_FX_LOCK_FILE_NAME;
	
	//native libraries
	public static final String ALIENFX_NATIVE_LIBRARY_NAME = "Alien";
	public static final String ALIENFX_NATIVE_LIBRARY_FOLDER = "lib";
	public static final String ALIENFX_NATIVE_LIBRARY = ALIENFX_NATIVE_LIBRARY_NAME + AlienFXProperties.arch;
	
	//powermodes and region ids
	public static final String ALIEN_FX_DEFAULT_POWER_MODE = "";
	public static final String POWER_BUTTON_ID = "PB";
	public static final String POWER_BUTTON_EYES_ID = "PBE";
	public static final String MEDIA_BAR_ID = "MB";
	public static final String TOUCH_PAD_ID = "TP";
	public static final String ALIEN_LOGO_ID = "AL";
	public static final String ALIEN_HEAD_ID = "AH";
	public static final String LEFT_SPEAKER_ID = "LS";
	public static final String RIGHT_SPEAKER_ID = "RS";
	public static final String LEFT_CENTER_KEYBOARD_ID = "LCK";
	public static final String LEFT_KEYBOARD_ID = "LK";
	public static final String RIGHT_CENTER_KEYBOARD_ID = "RCK";
	public static final String RIGHT_KEYBOARD_ID = "RK";
	
	public static final String ON_BATTERY_ID = "BAT";
	public static final String CHARGING_ID = "CH";
	public static final String AC_POWER_ID = "AC";
	public static final String STANDBY_ID = "SB";
}
