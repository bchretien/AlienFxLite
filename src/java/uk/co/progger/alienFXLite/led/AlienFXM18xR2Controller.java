package uk.co.progger.alienFXLite.led;

import java.util.LinkedList;

import uk.co.progger.alienFXLite.AlienFXProperties;
import uk.co.progger.alienFXLite.gui.AlienFXTexts;

public class AlienFXM18xR2Controller extends AlienFXController {

	private static final int STATE_BUSY = 0x11;
	private static final int STATE_READY = 0x10;
	private static final int STATE_UNKNOWN_COMMAND = 0x12;
	
	private static final int SUPPORTED_COMMANDS = 15;
	private static final byte COMMAND_END_STORAGE = 0x00;// = End Storage block (See storage)
	private static final byte COMMAND_SET_MORPH_COLOR = 0x01;// = Set morph color (See set commands)
	private static final byte COMMAND_SET_BLINK_COLOR = 0x02;// = Set blink color (See set commands)
	private static final byte COMMAND_SET_COLOR = 0x03;// = Set color (See set commands)
	private static final byte COMMAND_LOOP_BLOCK_END = 0x04;// = Loop Block end (See loops)
	private static final byte COMMAND_TRANSMIT_EXECUTE = 0x05;// = End transmition and execute
	private static final byte COMMAND_GET_STATUS = 0x06;// = Get device status (see get device status)
	private static final byte COMMAND_RESET = 0x07;// = Reset (See reset)
	private static final byte COMMAND_SAVE_NEXT = 0x08;// = Save next instruction in storage block (see storage)
	private static final byte COMMAND_SAVE = 0x09;// = Save storage data (See storage)
	private static final byte COMMAND_BATTERY_STATE = 0x0F;// = Set batery state (See set commands)
	private static final byte COMMAND_SET_SPEED = 0x0E;// = Set display speed (see set speed)
	
	public static final int RESET_TOUCH_CONTROLS = 0x01;
	public static final int RESET_SLEEP_LIGHTS_ON = 0x02;
	public static final int RESET_ALL_LIGHTS_OFF = 0x03;
	public static final int RESET_ALL_LIGHTS_ON = 0x04;
	
	private static final int DATA_LENGTH = 9;
	
	private static final byte START_BYTE = 0x02;
	private static final byte FILL_BYTE = 0x00;
	
	private static final int BLOCK_LOAD_ON_BOOT = 0x01;
	private static final int BLOCK_STANDBY = 0x02;
	private static final int BLOCK_AC_POWER = 0x05;
	private static final int BLOCK_CHARGING = 0x06;
	private static final int BLOCK_BATT_CRITICAL = 0x07;
	private static final int BLOCK_BAT_POWER = 0x08;
	  
	public static final int REGION_RIGHT_KEYBOARD = 0x0001; 
	public static final int REGION_MIDDLE_RIGHT_KEYBOARD = 0x0002; 
	public static final int REGION_LEFT_KEYBOARD = 0x0008; 
	public static final int REGION_MIDDLE_LEFT_KEYBOARD = 0x0004; 
	public static final int REGION_POWER_BUTTON_2 = 0x0010; 
	public static final int REGION_RIGHT_SPEAKER = 0x0020; 
	public static final int REGION_LEFT_SPEAKER = 0x0040;
	public static final int REGION_ALIEN_HEAD = 0x0080; 
	public static final int REGION_ALIEN_NAME = 0x0100; 
	public static final int REGION_TOUCH_PAD = 0x0200; 
	public static final int REGION_MEDIA_BAR = 0x1c00;
	public static final int REGION_POWER_BUTTON = 0x2000; 
	public static final int REGION_POWER_BUTTON_EYES = 0x4000; 

	public static final int REGION_ALL_BUT_POWER = 0x0f9fff; 
	
	public AlienFXM18xR2Controller() {
		super("Alienware M18x R2", STATE_BUSY, STATE_READY, STATE_UNKNOWN_COMMAND,
				COMMAND_END_STORAGE, COMMAND_SET_MORPH_COLOR, COMMAND_SET_BLINK_COLOR,
				COMMAND_SET_COLOR, COMMAND_LOOP_BLOCK_END, COMMAND_TRANSMIT_EXECUTE,
				COMMAND_GET_STATUS, COMMAND_RESET, COMMAND_SAVE_NEXT, COMMAND_SAVE,
				COMMAND_BATTERY_STATE, COMMAND_SET_SPEED, RESET_TOUCH_CONTROLS,
				RESET_SLEEP_LIGHTS_ON, RESET_ALL_LIGHTS_OFF, RESET_ALL_LIGHTS_ON, DATA_LENGTH, START_BYTE, FILL_BYTE);
		
		//NOTE: The default mode HAS to have an empty name. Otherwise the code wont know which is default.
		//TODO: change to use a boolean value instead
		AlienFXPowerMode normal = new AlienFXPowerMode(AlienFXProperties.ALIEN_FX_DEFAULT_POWER_MODE,AlienFXProperties.ALIEN_FX_DEFAULT_POWER_MODE, BLOCK_LOAD_ON_BOOT);
		AlienFXPowerMode standby = new AlienFXPowerMode(AlienFXProperties.STANDBY_ID, AlienFXTexts.STAND_BY_DESCRIPTION, BLOCK_STANDBY);
		AlienFXPowerMode acPower = new AlienFXPowerMode(AlienFXProperties.AC_POWER_ID, AlienFXTexts.AC_POWER_DESCRIPTION, BLOCK_AC_POWER);
		AlienFXPowerMode charging = new AlienFXPowerMode(AlienFXProperties.CHARGING_ID, AlienFXTexts.CHARGING2_DESCRIPTION, BLOCK_CHARGING);
		AlienFXPowerMode onBat = new AlienFXPowerMode(AlienFXProperties.ON_BATTERY_ID, AlienFXTexts.ON_BATTERY_DESCRIPTION, BLOCK_BAT_POWER);
		
		LinkedList<AlienFXPowerMode> powermodes = new LinkedList<AlienFXPowerMode>();
		powermodes.add(normal);
		LinkedList<AlienFXRegion> regions = new LinkedList<AlienFXRegion>();
		
		regions.add(new AlienFXRegion(AlienFXProperties.RIGHT_KEYBOARD_ID,  AlienFXTexts.RIGHT_KEYBOARD_DESCRIPTION, REGION_RIGHT_KEYBOARD,SUPPORTED_COMMANDS,true,true,true, powermodes));
		regions.add(new AlienFXRegion(AlienFXProperties.RIGHT_CENTER_KEYBOARD_ID, AlienFXTexts.RIGHT_CENTER_KEYBOARD_DESCRIPTION, REGION_MIDDLE_RIGHT_KEYBOARD,SUPPORTED_COMMANDS,true,true,true, powermodes));
		regions.add(new AlienFXRegion(AlienFXProperties.LEFT_KEYBOARD_ID,  AlienFXTexts.LEFT_KEYBOARD_DESCRIPTION, REGION_LEFT_KEYBOARD,SUPPORTED_COMMANDS,true,true,true, powermodes));
		regions.add(new AlienFXRegion(AlienFXProperties.LEFT_CENTER_KEYBOARD_ID, AlienFXTexts.LEFT_CENTER_KEYBOARD_DESCRIPTION, REGION_MIDDLE_LEFT_KEYBOARD,SUPPORTED_COMMANDS,true,true,true, powermodes));
		regions.add(new AlienFXRegion(AlienFXProperties.RIGHT_SPEAKER_ID,  AlienFXTexts.RIGHT_SPEAKER_DESCRIPTION, REGION_RIGHT_SPEAKER,SUPPORTED_COMMANDS,true,true,true, powermodes));
		regions.add(new AlienFXRegion(AlienFXProperties.LEFT_SPEAKER_ID,  AlienFXTexts.LEFT_SPEAKER_DESCRIPTION, REGION_LEFT_SPEAKER,SUPPORTED_COMMANDS,true,true,true, powermodes));
		regions.add(new AlienFXRegion(AlienFXProperties.ALIEN_HEAD_ID,  AlienFXTexts.ALIENWARE_HEAD_DESCRIPTION, REGION_ALIEN_HEAD,SUPPORTED_COMMANDS,true,true,true, powermodes));
		regions.add(new AlienFXRegion(AlienFXProperties.ALIEN_LOGO_ID,  AlienFXTexts.ALIENWARE_LOGO_DESCRIPTION, REGION_ALIEN_NAME,SUPPORTED_COMMANDS,true,true,true, powermodes));
		regions.add(new AlienFXRegion(AlienFXProperties.TOUCH_PAD_ID,  AlienFXTexts.TOUCHPAD_DESCRIPTION, REGION_TOUCH_PAD,SUPPORTED_COMMANDS,true,true,true, powermodes));
		regions.add(new AlienFXRegion(AlienFXProperties.MEDIA_BAR_ID,  AlienFXTexts.MEDIA_BAR_DESCRIPTION, REGION_MEDIA_BAR,SUPPORTED_COMMANDS,true,true,true, powermodes));
		regions.add(new AlienFXRegion(AlienFXProperties.POWER_BUTTON_EYES_ID, AlienFXTexts.ALIENWARE_POWERBUTTON_EYES_DESCRIPTION, REGION_POWER_BUTTON_EYES,1,false,false,true, powermodes));
		
		powermodes.clear();
		powermodes.add(standby);
		powermodes.add(acPower);
		powermodes.add(charging);
		powermodes.add(onBat);
		
		regions.add(new AlienFXRegion(AlienFXProperties.POWER_BUTTON_ID, AlienFXTexts.POWER_BUTTON_DESCRIPTION, REGION_POWER_BUTTON,1,false,false,true, powermodes));
		
		setRegions(regions);
	}

	@Override
	public int deviceId() {
		return LEDController.M18XR2_ALIENFX;
	}
}
