package uk.co.progger.alienFXLite.led;

import java.util.LinkedList;

import uk.co.progger.alienFXLite.AlienFXProperties;
import uk.co.progger.alienFXLite.gui.AlienFXTexts;

public class AlienFXArea51Controller extends AlienFXController{

	private static final int SUPPORTED_COMMANDS = 15;
	private static final String KEY_BOARD = "KeyBoard";
	private static final String KB = "KB";
	private static final String LIGHTPIPE = "Lightpipe";
	private static final String LP = "LP";
	private static final int STATE_BUSY = 0x11;
	private static final int STATE_READY = 0x10;
	private static final int STATE_UNKNOWN_COMMAND = 0x12;
	
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
	
	private static final byte START_BYTE = 0x00;
	private static final byte FILL_BYTE = 0x00;
	
	private static final int BLOCK_LOAD_ON_BOOT = 0x01;
	private static final int BLOCK_STANDBY = 0x02;
	private static final int BLOCK_AC_POWER = 0x05;
	private static final int BLOCK_CHARGING = 0x06;
	private static final int BLOCK_BAT_POWER = 0x08;
	
	private static final int REGION_TOUCH_PAD = 0x000001;
	private static final int REGION_LIGHTPIPE = 0x000020; 
	private static final int REGION_ALIEN_LOGO = 0x000080; 
	private static final int REGION_ALIEN_HEAD = 0x000100;
	private static final int REGION_KEY_BOARD = 0x000400;
	private static final int REGION_TOUCH_PANEL = 0x010000; 
	private static final int REGION_POWER_BUTTON = 0x008000;
	
	public AlienFXArea51Controller() {
		super("AllPowerfull Alienware", STATE_BUSY, STATE_READY, STATE_UNKNOWN_COMMAND,
				COMMAND_END_STORAGE, COMMAND_SET_MORPH_COLOR, COMMAND_SET_BLINK_COLOR,
				COMMAND_SET_COLOR, COMMAND_LOOP_BLOCK_END, COMMAND_TRANSMIT_EXECUTE,
				COMMAND_GET_STATUS, COMMAND_RESET, COMMAND_SAVE_NEXT, COMMAND_SAVE,
				COMMAND_BATTERY_STATE, COMMAND_SET_SPEED, RESET_TOUCH_CONTROLS,
				RESET_SLEEP_LIGHTS_ON, RESET_ALL_LIGHTS_OFF, RESET_ALL_LIGHTS_ON, DATA_LENGTH, START_BYTE, FILL_BYTE);
		
		AlienFXPowerMode normal = new AlienFXPowerMode(AlienFXProperties.ALIEN_FX_DEFAULT_POWER_MODE,AlienFXProperties.ALIEN_FX_DEFAULT_POWER_MODE, BLOCK_LOAD_ON_BOOT);
		AlienFXPowerMode standby = new AlienFXPowerMode(AlienFXProperties.STANDBY_ID, AlienFXTexts.STAND_BY_DESCRIPTION, BLOCK_STANDBY);
		AlienFXPowerMode acPower = new AlienFXPowerMode(AlienFXProperties.AC_POWER_ID, AlienFXTexts.AC_POWER_DESCRIPTION, BLOCK_AC_POWER);
		AlienFXPowerMode charging = new AlienFXPowerMode(AlienFXProperties.CHARGING_ID, AlienFXTexts.CHARGING2_DESCRIPTION, BLOCK_CHARGING);
		AlienFXPowerMode onBat = new AlienFXPowerMode(AlienFXProperties.ON_BATTERY_ID, AlienFXTexts.ON_BATTERY_DESCRIPTION, BLOCK_BAT_POWER);
		
		LinkedList<AlienFXPowerMode> powermodes = new LinkedList<AlienFXPowerMode>();
		powermodes.add(normal);
		LinkedList<AlienFXRegion> regions = new LinkedList<AlienFXRegion>();
		
		regions.add(new AlienFXRegion(LP,LIGHTPIPE, REGION_LIGHTPIPE,SUPPORTED_COMMANDS,true,true,true, powermodes));
		regions.add(new AlienFXRegion(KB,KEY_BOARD, REGION_KEY_BOARD,SUPPORTED_COMMANDS,true,true,true, powermodes));
		regions.add(new AlienFXRegion(AlienFXProperties.ALIEN_HEAD_ID,  AlienFXTexts.ALIENWARE_HEAD_DESCRIPTION, REGION_ALIEN_HEAD,SUPPORTED_COMMANDS,true,true,true, powermodes));
		regions.add(new AlienFXRegion(AlienFXProperties.ALIEN_LOGO_ID,  AlienFXTexts.ALIENWARE_LOGO_DESCRIPTION, REGION_ALIEN_LOGO,SUPPORTED_COMMANDS,true,true,true, powermodes));
		regions.add(new AlienFXRegion(AlienFXProperties.TOUCH_PAD_ID,  AlienFXTexts.TOUCHPAD_DESCRIPTION, REGION_TOUCH_PAD,SUPPORTED_COMMANDS,true,true,true, powermodes));
		regions.add(new AlienFXRegion(AlienFXProperties.MEDIA_BAR_ID,  AlienFXTexts.MEDIA_BAR_DESCRIPTION, REGION_TOUCH_PANEL,SUPPORTED_COMMANDS,true,true,true, powermodes));
		
		powermodes.clear();
		powermodes.add(standby);
		powermodes.add(acPower);
		powermodes.add(charging);
		powermodes.add(onBat);
		
		regions.add(new AlienFXRegion(AlienFXProperties.POWER_BUTTON_ID, AlienFXTexts.POWER_BUTTON_DESCRIPTION, REGION_POWER_BUTTON,1,false,false,true, powermodes));
		setRegions(regions);
	}

	public int deviceId() {
		return LEDController.AREA51_ALIENFX;
	}
}
