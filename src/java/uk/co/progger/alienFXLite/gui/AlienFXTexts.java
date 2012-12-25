package uk.co.progger.alienFXLite.gui;

public class AlienFXTexts {
	
	//errors:
	public static final String ALIEN_FX_ERROR_TITLE_TEXT = "AlienFX error";
	public static final String DATA_LENGTH_ERROR_FORMAT = "Data length should be %d but was %d";
	public static final String DEVICE_NOT_PRESENT_ERROR_TEXT = "The application was unable to communicate with the alienFX device. The device is either not present, or the application lacks sufficient rights to access the device";
	public static final String COMMUNICATION_ERROR_FORMAT = "Error occured while trying to communicate with the AlienFX device: %s\n";
	public static final Object DEVICE_PERMISSION_ERROR_TEXT = "The Device was found, but the application was unable to communicate with the alienFX controller. Do you have the right to access the alienfx device(e.g. are you running as admin)";
	public static final String SYSTEM_UI_NOT_FOUND = "System UI not Found";
	public static final String PROFILE_EXISTS_ERROR_TEXT = "Profile with that name already exists";
	public static final String PROFILE_NAME_EMPTY_ERROR_TEXT = "Name cannot be empty";
	public static final String SAVE_PROFILE_ERROR_FORMAT = "Failed to save the Profile: %s\n";
	public static final String RELOAD_PROFILE_ERROR_FORMAT = "Failed to reload the Profile: %s\n";
	public static final Object ALREADY_RUNNING_ERROR_TEXT = "AlienFX is already running";
	
	//warnings
	public static final String ALIEN_FX_WARNING_TITLE_TEXT = "AlienFX Warning";
	public static final String SYSTEM_TRAY_WARNING_TEXT = "It seems the system does not support system trays. The Application will not run in background.";
	
	//info messages
	public static final String ALIEN_FX_INFO_TITLE_TEXT = "AlienFx Info";
	public static final String SHOW_ALIEN_FX_LITE_TEXT = "Show AlienFXLite";
	public static final String ALIEN_FX_BACKGROUND_TEXT = "Still running in the background";
	public static final String ENTER_NAME_TITLE_TEXT = "Enter Name";
	public static final String ENTER_NAME_TEXT = "Enter a name for the new profile";
	public static final String USAGE_TITLE = "Usage";
	public static final String USAGE = "The Application is very similar in use as the AlienFX application developed by Alienware. \nFirst, create a new profile. Then you can select the colors for the given regions of your computer. \nAdding an action (Color, Blink, Morph) is done by pressing one of the small dropdown buttons.\nTo copy paste a given sequence of actions, just select the actions by pressing on the section they are in, and drag the mouse over them.\n Additionally, one can use modifiers such as shift and control to modify the way in which selection behaves. \nTo paste the selected section, click on the add button and then select the paste icon (the last one).\n Additionally, one can easily change the colors of profile by selecting a new color and pressing on a color in the Color used panel.\n This will change all actions with the color pressed on, to the selected color. \nPressing with the right mouse button on any color button or action will result in selecting that color.\nFinally, if you get weird behaviour, reset the AlienFX device by pressing on the Reset button under the help menu";
	public static final String ABOUT_FORMAT = "AlienFX Lite %s developed by %s";
	public static final String ABOUT_TITLE = "About";

	
	//controls
	public static final String EXIT_TEXT = "Exit";
	public static final String PREVIEW_LABEL_TEXT = "Preview";
	public static final String SELECT_PROFILE_TEXT = "Please select a profile from the combobox or create a new one";
	
	public static final String APPLY_THE_CURRENT_PROFILE = "Apply the current Profile";
	public static final String DELETE_THE_CURRENT_PROFILE = "Delete the current Profile";
	public static final String SAVE_THE_CURRENT_PROFILE = "Save the current Profile";
	public static final String RELOAD_THE_CURRENT_PROFILE = "Reload the current Profile";
	public static final String CREATE_A_NEW_PROFILE = "Create a new Profile";
	
	public static final String PROFILE_SPEED_TEXT = "Speed:";
	public static final String COLORS_PROFILE_TITLE = "Colors Used in Profile";
	public static final String DEFAULT_TEXT = "Default";
	public static final String COLORS_TEXT = "Colors";
	public static final String PROFILE_TEXT = "Profile";
	
	public static final String PROFILE_SPEED_SLOW = "Slow";
	public static final String PROFILE_SPEED_FAST = "Fast";
	
	public static final String ACTION_COLOR_TEXT = "Color";
	public static final String ACTION_BLINK_TEXT = "Blink";
	public static final String ACTION_MORPH_TEXT = "Morph";
	
	// tooltips
	public static final String COLOR_TOOLTIP = "Fixed color";
	public static final String BLINK_TOOLTIP = "Blinking color";
	public static final String MORPH_TOOLTIP = "Morphing color";
	public static final String PASTE_TOOLTIP = "Paste selected effect";

	//Alienware devices:
	public static final String POWER_BUTTON_DESCRIPTION = "Power Button";
	public static final String ALIENWARE_POWERBUTTON_EYES_DESCRIPTION = "Powerbutton Eyes";
	public static final String MEDIA_BAR_DESCRIPTION = "Media Bar";
	public static final String TOUCHPAD_DESCRIPTION = "Touchpad";
	public static final String ALIENWARE_LOGO_DESCRIPTION = "Alienware logo";
	public static final String ALIENWARE_HEAD_DESCRIPTION = "Alienware head";
	public static final String LEFT_SPEAKER_DESCRIPTION = "Left Speaker";
	public static final String RIGHT_SPEAKER_DESCRIPTION = "Right Speaker";
	public static final String LEFT_CENTER_KEYBOARD_DESCRIPTION = "Left Center Keyboard";
	public static final String LEFT_KEYBOARD_DESCRIPTION = "Left Keyboard";
	public static final String RIGHT_CENTER_KEYBOARD_DESCRIPTION = "Right Center Keyboard";
	public static final String RIGHT_KEYBOARD_DESCRIPTION = "Right Keyboard";

	public static final String ON_BATTERY_DESCRIPTION = "On Battery";
	public static final String CHARGING2_DESCRIPTION = "Charging";
	public static final String AC_POWER_DESCRIPTION = "AC Power";
	public static final String STAND_BY_DESCRIPTION = "StandBy";
	
}
