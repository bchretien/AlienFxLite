package uk.co.progger.alienFXLite.led;

import uk.co.progger.alienFXLite.AlienFXProperties;

class LEDController {
	
	static {
		System.setProperty("java.library.path", AlienFXProperties.ALIENFX_NATIVE_LIBRARY_FOLDER);
        System.loadLibrary(AlienFXProperties.ALIENFX_NATIVE_LIBRARY);
    }	
	
	public static final int ALLPOWERFULL_ALIENFX = 1;
	public static final int AREA51_ALIENFX = 2;
	public static final int NOT_FOUND = 0;
	
	/**
	 * Initializes the LED controller.
	 * @return - ALLPOWERFULL_ALIENFX if the All powerfull alienfx controller was found, AREA51_ALIENFX if the area 51 laptop alienfx controller was found, NOT_FOUND if there was an error finidng the controller
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
