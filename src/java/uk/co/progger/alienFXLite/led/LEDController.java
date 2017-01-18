package uk.co.progger.alienFXLite.led;

import uk.co.progger.alienFXLite.AlienFXProperties;

class LEDController {
	
	static {
		System.setProperty("java.library.path", AlienFXProperties.ALIENFX_NATIVE_LIBRARY_FOLDER);
        System.loadLibrary(AlienFXProperties.ALIENFX_NATIVE_LIBRARY);
    }	
	
	// Alienware AlienFX pids (as given by lsusb)
	public static final int ALLPOWERFULL_ALIENFX = 0x0512;

	public static final int AREA51_ALIENFX = 0x0511;

	public static final int M11XR1_ALIENFX = 0x0514;
	public static final int M11XR2_ALIENFX = 0x0515;
	public static final int M11XR3_ALIENFX = 0x0516;

	public static final int M14XR2_ALIENFX = 0x0521;
	public static final int M14XR3_ALIENFX = 0x0525;

	public static final int M17X_ALIENFX   = 0x0524;
	public static final int M17XR2_ALIENFX = 0x0520;
	public static final int M17XR3_ALIENFX = 0x0528;
	public static final int M17XR4_ALIENFX = 0x0530;

	public static final int M18XR2_ALIENFX = 0x0518;

	public static final int NOT_FOUND = -1;

	/**
	 * Initializes the LED controller.
	 * @return - AlienFX PID
	 */
	native static int initialize();
	
	/**
	 * Method writes data to the device.
	 * @param data - the data to be written in form of an array
	 * @param dataSize - the size of the data
	 * @return The number of bytes written. 
	 */
	native static int write(byte[] data);
	
	static int writeDebug(byte[] data){
		if(AlienFXProperties.isDebug){
			System.out.print("Writing ");
			for(byte b : data){
				System.out.printf("0x%x ", b);
			}
			
			System.out.println();
//			return 9;
		}
		return write(data);
	}

	/**
	 * Method reads the data from the device. 
	 * @return The data which was read from the device
	 */
	native static byte[] read();
	
	/**
	 * deallocates all the memory used. Only the initialize call is valid after destroy has been called
	 */
	native static void destroy();
}
