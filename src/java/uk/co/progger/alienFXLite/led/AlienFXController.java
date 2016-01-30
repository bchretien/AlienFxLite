package uk.co.progger.alienFXLite.led;

import java.awt.Color;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import uk.co.progger.alienFXLite.gui.AlienFXTexts;

public abstract class AlienFXController {
	private static final String INVALID_INPUT = "Invalid Input";
	
	public static final ConcurrentHashMap<String, AlienFXRegion> regionLookUp = new ConcurrentHashMap<String, AlienFXRegion>();
	public static final ConcurrentHashMap<String, AlienFXPowerMode> powermodeLookUp = new ConcurrentHashMap<String, AlienFXPowerMode>();
	
	public final String description;
	
	//the maximal possible speed
	public static final int MAX_SPEED = 1000;
	public static final int MIN_SPEED = 100;
	public static final int STEP_SPEED = 100;
	
	private final int STATE_BUSY;
	@SuppressWarnings("unused")
	private final int STATE_READY;
	@SuppressWarnings("unused")
	private final int STATE_UNKNOWN_COMMAND;
	
	@SuppressWarnings("unused")
	private final byte COMMAND_END_STORAGE;
	private final byte COMMAND_SET_MORPH_COLOR;
	private final byte COMMAND_SET_BLINK_COLOR;
	private final byte COMMAND_SET_COLOR;
	private final byte COMMAND_LOOP_BLOCK_END;
	private final byte COMMAND_TRANSMIT_EXECUTE;
	private final byte COMMAND_GET_STATUS;
	private final byte COMMAND_RESET;
	private final byte COMMAND_SAVE_NEXT;
	private final byte COMMAND_SAVE;
	@SuppressWarnings("unused")
	private final byte COMMAND_BATTERY_STATE;
	private final byte COMMAND_SET_SPEED;
	
	public final int RESET_TOUCH_CONTROLS;
	public final int RESET_SLEEP_LIGHTS_ON;
	public final int RESET_ALL_LIGHTS_OFF;
	public final int RESET_ALL_LIGHTS_ON;
	
	private final int DATA_LENGTH;
	
	private final byte START_BYTE;
	private final byte FILL_BYTE;
	
	private LinkedList<AlienFXRegion> alienFxregions;
	
	/**
	 * Creates a new controller
	 */	
	public AlienFXController(String description, int sTATEBUSY, int sTATEREADY,
			int sTATEUNKNOWNCOMMAND, byte cOMMANDENDSTORAGE,
			byte cOMMANDSETMORPHCOLOR, byte cOMMANDSETBLINKCOLOR,
			byte cOMMANDSETCOLOR, byte cOMMANDLOOPBLOCKEND,
			byte cOMMANDTRANSMITEXECUTE, byte cOMMANDGETSTATUS,
			byte cOMMANDRESET, byte cOMMANDSAVENEXT, byte cOMMANDSAVE,
			byte cOMMANDBATTERYSTATE, byte cOMMANDSETSPEED,
			int rESETTOUCHCONTROLS, int rESETSLEEPLIGHTSON,
			int rESETALLLIGHTSOFF, int rESETALLLIGHTSON, int dATALENGTH,
			byte sTARTBYTE, byte fILLBYTE) {
		super();
		this.description = description;
		STATE_BUSY = sTATEBUSY;
		STATE_READY = sTATEREADY;
		STATE_UNKNOWN_COMMAND = sTATEUNKNOWNCOMMAND;
		COMMAND_END_STORAGE = cOMMANDENDSTORAGE;
		COMMAND_SET_MORPH_COLOR = cOMMANDSETMORPHCOLOR;
		COMMAND_SET_BLINK_COLOR = cOMMANDSETBLINKCOLOR;
		COMMAND_SET_COLOR = cOMMANDSETCOLOR;
		COMMAND_LOOP_BLOCK_END = cOMMANDLOOPBLOCKEND;
		COMMAND_TRANSMIT_EXECUTE = cOMMANDTRANSMITEXECUTE;
		COMMAND_GET_STATUS = cOMMANDGETSTATUS;
		COMMAND_RESET = cOMMANDRESET;
		COMMAND_SAVE_NEXT = cOMMANDSAVENEXT;
		COMMAND_SAVE = cOMMANDSAVE;
		COMMAND_BATTERY_STATE = cOMMANDBATTERYSTATE;
		COMMAND_SET_SPEED = cOMMANDSETSPEED;
		RESET_TOUCH_CONTROLS = rESETTOUCHCONTROLS;
		RESET_SLEEP_LIGHTS_ON = rESETSLEEPLIGHTSON;
		RESET_ALL_LIGHTS_OFF = rESETALLLIGHTSOFF;
		RESET_ALL_LIGHTS_ON = rESETALLLIGHTSON;
		DATA_LENGTH = dATALENGTH;
		START_BYTE = sTARTBYTE;
		FILL_BYTE = fILLBYTE;
	}
	
	public abstract int deviceId();
	
	/**
	 * set the regions for this device
	 * @param regions
	 */
	protected void setRegions(LinkedList<AlienFXRegion> regions){
		alienFxregions = new LinkedList<AlienFXRegion>(regions);
	}
	
	/**
	 * Returns a list of all supported regions by the device
	 * @return
	 */
	public LinkedList<AlienFXRegion> getAlienFxRegions(){
		return new LinkedList<AlienFXRegion>(alienFxregions);
	}

	/**
	 * This command tells the alienfx device to save the next instruction which comes after this instruction.
	 * @param block - into which block to save
	 * @return
	 * @throws AlienFXCommunicationException
	 */
	public synchronized boolean saveNextInstruction(int block) throws AlienFXCommunicationException{
		int len = LEDController.writeDebug(fill(new byte[]{START_BYTE,COMMAND_SAVE_NEXT ,(byte)block}));
		if(len != DATA_LENGTH)
			throw new AlienFXCommunicationException(String.format(AlienFXTexts.DATA_LENGTH_ERROR_FORMAT, DATA_LENGTH ,len));
		return len == DATA_LENGTH;
	}
	
	/**
	 * Commands tells the alienFX device that the last instructions were part of a loop
	 * @return
	 * @throws AlienFXCommunicationException
	 */
	public synchronized boolean finishLoop() throws AlienFXCommunicationException{
		int len = LEDController.writeDebug(fill(new byte[]{START_BYTE,COMMAND_LOOP_BLOCK_END}));
		if(len != DATA_LENGTH)
			throw new AlienFXCommunicationException(String.format(AlienFXTexts.DATA_LENGTH_ERROR_FORMAT, DATA_LENGTH ,len));
		return len == DATA_LENGTH;
	}
	
	/**
	 * Command which should be called once you are done saving stuff into a block
	 * @return
	 * @throws AlienFXCommunicationException
	 */
	public synchronized boolean finishSave() throws AlienFXCommunicationException{
		int len = LEDController.writeDebug(fill(new byte[]{START_BYTE,COMMAND_SAVE}));
		if(len != DATA_LENGTH)
			throw new AlienFXCommunicationException(String.format(AlienFXTexts.DATA_LENGTH_ERROR_FORMAT, DATA_LENGTH ,len));
		return len == DATA_LENGTH;
	}
	
	/**
	 * Reset the lights. This has to be called before any other operation
	 * @param resetType - the reset type. Can be on of RESET_CONTROLS_ON RESET_SLEEP_LIGHTS_ON RESET_ALL_LIGHTS_OFF RESET_ALL_LIGHTS_ON
	 * @return true if the reset was successful, false otherwise
	 * @throws AlienFXCommunicationException 
	 */
	public synchronized boolean reset(int resetType) throws AlienFXCommunicationException{
		if(!( resetType == RESET_TOUCH_CONTROLS || 
		      resetType == RESET_SLEEP_LIGHTS_ON || 
		      resetType == RESET_ALL_LIGHTS_OFF || 
		      resetType == RESET_ALL_LIGHTS_ON))
			throw new RuntimeException(INVALID_INPUT);
	
		int len = LEDController.writeDebug(fill(new byte[]{START_BYTE,COMMAND_RESET ,(byte)resetType}));
		if(len != DATA_LENGTH)
			throw new AlienFXCommunicationException(String.format(AlienFXTexts.DATA_LENGTH_ERROR_FORMAT, DATA_LENGTH ,len));
		return len == DATA_LENGTH;
	}


	/***
	 * Convenience function to set all the lights to a given color
	 * @param r - the red color. ranging from 0x00 - 0xFF
	 * @param g
	 * @param b
	 * @return
	 * @throws AlienFXCommunicationException 
	 */
	public synchronized boolean setAllLightsWithColor(int block, int r, int g, int b) throws AlienFXCommunicationException{
		int RG = r & 0xF0;
		RG = RG | ((g >> 4) & 0x0F);
		int B = b & 0xF0;
		
		int len = LEDController.writeDebug(new byte[]{START_BYTE, COMMAND_SET_COLOR, (byte)(block), (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)RG, (byte)B,0});
		if(len != DATA_LENGTH)
			throw new AlienFXCommunicationException(String.format(AlienFXTexts.DATA_LENGTH_ERROR_FORMAT, DATA_LENGTH ,len));
		return len == DATA_LENGTH;
	}
	
	
	public synchronized void waitForReady() throws AlienFXCommunicationException{
		while(isBusy()){
			try{Thread.sleep(40);}catch(Exception e){};
		}
	}
	
	/**
	 * Convenience function. speed between 0 and 1. 1 - fastest, 0 slowest.
	 * @param speed
	 * @throws AlienFXCommunicationException 
	 */
	public synchronized void setSpeed(double speed) throws AlienFXCommunicationException{
		setSpeed((int)(1000*speed));
	}
	
	/**
	 * Set the speed of the theme
	 * @param speed - the speed has to be between 1 and MAX_SPEED. The bigger the number, the slower the speed
	 * @throws AlienFXCommunicationException 
	 */
	public synchronized void setSpeed(int speed) throws AlienFXCommunicationException{
		if(speed > MAX_SPEED) speed = MAX_SPEED;
		if(speed < MIN_SPEED) speed = MIN_SPEED;
		speed = speed /STEP_SPEED;
		speed = speed *STEP_SPEED;
		int b1 = (speed >> 8) & 0xFF;
		int b2 = speed & 0xFF;
		int len = LEDController.writeDebug(fill(new byte[]{START_BYTE, COMMAND_SET_SPEED, (byte)b1, (byte)b2}));
		if(len != DATA_LENGTH)
			throw new AlienFXCommunicationException(String.format(AlienFXTexts.DATA_LENGTH_ERROR_FORMAT, DATA_LENGTH ,len));
	}

	/**
	 * Convenience function. Same as setBlinkLightWithColor(int, int, int, int). Here the values range from 0-1
	 * @param region
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 * @throws AlienFXCommunicationException 
	 */
	public synchronized boolean setMorphLightWithColor(int block, int region, double r, double g, double b, double r2, double g2, double b2) throws AlienFXCommunicationException{
		if(r > 1) r = 1;
		if(r < 0) r = 0;
		
		if(g > 1) g = 1;
		if(g < 0) g =0;
		
		if(b > 1) b = 1;
		if(b < 0) b = 0;
		
		if(r2 > 1) r2 = 1;
		if(r2 < 0) r2 = 0;
		
		if(g2 > 1) g2 = 1;
		if(g2 < 0) g2 =0;
		
		if(b2 > 1) b2 = 1;
		if(b2 < 0) b2 = 0;
		
		return setMorphLightWithColor(block, region, (int) r*0xFF,(int) g*0xFF, (int) b*0xFF, (int) r2*0xFF,(int) g2*0xFF, (int) b2*0xFF);
	}
	
	/**
	 * Convenience function, Same as setBlinkLightWithColor(int region, int r, int g, int b)
	 * @param region
	 * @param c
	 * @return
	 * @throws AlienFXCommunicationException 
	 */
	public synchronized boolean setMorphLightWithColor(int block, int region, Color c, Color c2) throws AlienFXCommunicationException{
		return setMorphLightWithColor(block, region, c.getRed(), c.getGreen(), c.getBlue(), c2.getRed(), c2.getGreen(), c2.getBlue());
	}
	
	/**
	 * Set the a given region with color blink defined by r g b. The valid values are from 0 - 0xFF (0 - 255).
	 * Note, that the LEDs will only see differences in the color as long as the values change by a factor of 16. In fact, the AlienFX controller
	 * uses only 4 bits per color, which only gives 16 steps per color, thus only 16x16x16 - 4096 possible colors. Note, these are not really different colors per se,
	 * as this coding also includes the intensity of the lights.
	 * @param block TODO
	 * @param region
	 * @param r - The amount of red
	 * @param g - The amount of green
	 * @param b - The amount of blue
	 * @return
	 * @throws AlienFXCommunicationException 
	 */
	public synchronized boolean setMorphLightWithColor(int block, int region, int r, int g, int b, int c2r, int c2g, int c2b) throws AlienFXCommunicationException{
		int RG = r & 0xF0;
		RG = RG | ((g >> 4) & 0x0F);
		int B = b & 0xF0;
		B = B | ((c2r >> 4) & 0x0F);
		
		int RG2 = c2g & 0xF0;
		RG2 = RG2 | (( c2b >> 4) & 0x0F);
			
		byte b1 = (byte)((region >> 16) & 0xFF);
		byte b2 = (byte)((region >> 8) & 0xFF);
		byte b3 = (byte)((region) & 0xFF);
		
		int len = LEDController.writeDebug(new byte[]{START_BYTE, COMMAND_SET_MORPH_COLOR, (byte)(block & 0xFF), b1, b2, b3, (byte)RG, (byte)B, (byte)RG2});
		if(len != DATA_LENGTH)
			throw new AlienFXCommunicationException(String.format(AlienFXTexts.DATA_LENGTH_ERROR_FORMAT, DATA_LENGTH ,len));
		return len == DATA_LENGTH;
	}
	
	/**
	 * Convenience function. Same as setBlinkLightWithColor(int, int, int, int). Here the values range from 0-1
	 * @param region
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 * @throws AlienFXCommunicationException 
	 */
	public synchronized boolean setBlinkLightWithColor(int block, int region, double r, double g, double b) throws AlienFXCommunicationException{
		if(r > 1) r = 1;
		if(r < 0) r = 0;
		
		if(g > 1) g = 1;
		if(g < 0) g =0;
		
		if(b > 1) b = 1;
		if(b < 0) b = 0;
		
		return setBlinkLightWithColor(block, region, (int) r*0xFF,(int) g*0xFF, (int) b*0xFF);
	}
	
	/**
	 * Convenience function, Same as setBlinkLightWithColor(int region, int r, int g, int b)
	 * @param region
	 * @param c
	 * @return
	 * @throws AlienFXCommunicationException 
	 */
	public synchronized boolean setBlinkLightWithColor(int block, int region, Color c) throws AlienFXCommunicationException{
		return setBlinkLightWithColor(block, region, c.getRed(), c.getGreen(), c.getBlue());
	}
	
	/**
	 * Set the a given region with color blink defined by r g b. The valid values are from 0 - 0xFF (0 - 255).
	 * Note, that the LEDs will only see differences in the color as long as the values change by a factor of 16. In fact, the AlienFX controller
	 * uses only 4 bits per color, which only gives 16 steps per color, thus only 16x16x16 - 4096 possible colors. Note, these are not really different colors per se,
	 * as this coding also includes the intensity of the lights.
	 * @param block TODO
	 * @param region
	 * @param r - The amount of red
	 * @param g - The amount of green
	 * @param b - The amount of blue
	 * @return
	 * @throws AlienFXCommunicationException 
	 */
	public synchronized boolean setBlinkLightWithColor(int block, int region, int r, int g, int b) throws AlienFXCommunicationException{
		int RG = r & 0xF0;
		RG = RG | ((g >> 4) & 0x0F);
		int B = b & 0xF0;
		
		byte b1 = (byte)((region >> 16) & 0xFF);
		byte b2 = (byte)((region >> 8) & 0xFF);
		byte b3 = (byte)((region) & 0xFF);
		
		int len = LEDController.writeDebug(new byte[]{START_BYTE, COMMAND_SET_BLINK_COLOR, (byte)(block & 0xFF), b1, b2, b3, (byte)RG, (byte)B,0});
		if(len != DATA_LENGTH)
			throw new AlienFXCommunicationException(String.format(AlienFXTexts.DATA_LENGTH_ERROR_FORMAT, DATA_LENGTH ,len));
		return len == DATA_LENGTH;
	}
	
	/**
	 * Convenience function. Same as setLightWithColor(int, int, int, int). Here the values range from 0-1
	 * @param region
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 * @throws AlienFXCommunicationException 
	 */
	public synchronized boolean setLightWithColor(int block, int region, double r, double g, double b) throws AlienFXCommunicationException{
		if(r > 1) r = 1;
		if(r < 0) r = 0;
		
		if(g > 1) g = 1;
		if(g < 0) g =0;
		
		if(b > 1) b = 1;
		if(b < 0) b = 0;
		
		return setLightWithColor(block, region, (int) r*0xFF,(int) g*0xFF, (int) b*0xFF);
	}
	
	/**
	 * Convenience function, Same as setLightWithColor(int region, int r, int g, int b)
	 * @param region
	 * @param c
	 * @return
	 * @throws AlienFXCommunicationException 
	 */
	public synchronized boolean setLightWithColor(int block, int region, Color c) throws AlienFXCommunicationException{
		return setLightWithColor(block, region, c.getRed(), c.getGreen(), c.getBlue());
	}
	
	/**
	 * Set the a given region with color defined by r g b. The valid values are from 0 - 0xFF (0 - 255).
	 * Note, that the LEDs will only see differences in the color as long as the values change by a factor of 16. In fact, the AlienFX controller
	 * uses only 4 bits per color, which only gives 16 steps per color, thus only 16x16x16 - 4096 possible colors. Note, these are not really different colors per se,
	 * as this coding also includes the intensity of the lights.
	 * @param block TODO
	 * @param region
	 * @param r - The amount of red
	 * @param g - The amount of green
	 * @param b - The amount of blue
	 * @return
	 * @throws AlienFXCommunicationException 
	 */
	public synchronized boolean setLightWithColor(int block, int region, int r, int g, int b) throws AlienFXCommunicationException{
		int RG = r & 0xF0;
		RG = RG | ((g >> 4) & 0x0F);
		int B = b & 0xF0;
		byte b1 = (byte)((region >> 16) & 0xFF);
		byte b2 = (byte)((region >> 8) & 0xFF);
		byte b3 = (byte)((region) & 0xFF);
		
		int len = LEDController.writeDebug(new byte[]{START_BYTE, COMMAND_SET_COLOR, (byte)(block & 0xFF), b1, b2, b3, (byte)RG, (byte)B,0});
		if(len != DATA_LENGTH)
			throw new AlienFXCommunicationException(String.format(AlienFXTexts.DATA_LENGTH_ERROR_FORMAT, DATA_LENGTH ,len));
		return len == DATA_LENGTH;
	}
	
	/**
	 * Transmit and execute the previously written commands
	 * @return
	 * @throws AlienFXCommunicationException 
	 */
	public synchronized boolean transmitAndExecute() throws AlienFXCommunicationException{
		int len = LEDController.writeDebug(fill(new byte[]{START_BYTE,COMMAND_TRANSMIT_EXECUTE}));
		if(len != DATA_LENGTH)
			throw new AlienFXCommunicationException(String.format(AlienFXTexts.DATA_LENGTH_ERROR_FORMAT, DATA_LENGTH ,len));
		return len == DATA_LENGTH;
	}
	
	/**
	 * Gets whether a controller is busy or not
	 * @return
	 * @throws AlienFXCommunicationException 
	 */
	public synchronized boolean isBusy() throws AlienFXCommunicationException{
		int len = LEDController.writeDebug(fill(new byte[]{START_BYTE, COMMAND_GET_STATUS}));
		if(len != DATA_LENGTH)
			throw new AlienFXCommunicationException(String.format(AlienFXTexts.DATA_LENGTH_ERROR_FORMAT, DATA_LENGTH ,len));
		if(len > 0)
			return LEDController.read()[0] == STATE_BUSY;
		return true;
	}
	
	/**
	 * A simple "ping" to check if the controller can be written to. Throws an exception if not.
	 * @throws AlienFXCommunicationException
	 */
	public synchronized void ping() throws AlienFXCommunicationException {
		int len = LEDController.writeDebug(fill(new byte[]{START_BYTE, COMMAND_GET_STATUS}));
		if(len != DATA_LENGTH)
			throw new AlienFXCommunicationException(String.format(AlienFXTexts.DATA_LENGTH_ERROR_FORMAT, DATA_LENGTH ,len));
	}
	
	/**
	 * Function will create a new array of size data length and fill the rest with FILL_BYTE data
	 * @param data - the first byte values
	 * @return the complete data which can be used to send to the AlienFX device
	 */
	private byte[] fill(byte[] data){
		byte[] newData = new byte[DATA_LENGTH];
		int i;
		for(i = 0; i < data.length; ++i)
			newData[i] = data[i];
		for(; i < DATA_LENGTH; ++i)
			newData[i] = FILL_BYTE;
		
		return newData;
	}
}
