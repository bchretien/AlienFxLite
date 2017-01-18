package uk.co.progger.alienFXLite.led;


/**
 * Simple class to get an alienware device
 * @author filipw
 *
 */
public class AlienFXControllerFactory {

	private static AlienFXController controller = null;
	
	/**
	 * Returns the AlienFx device for this computer. Will throw AlienFXControllerNotFoundException if no device could be found
	 * @return
	 * @throws AlienFXControllerNotFoundException
	 * @throws AlienFXCommunicationException 
	 */
	public synchronized static AlienFXController getAlienFXDevice()
		throws AlienFXControllerNotFoundException, AlienFXControllerUnknownDeviceException,
		       AlienFXCommunicationException
	{
		if(controller != null)
			return controller;
		
		int deviceId = LEDController.initialize();
		
		if(deviceId == LEDController.NOT_FOUND)
			throw new AlienFXControllerNotFoundException();
		
		else if (deviceId == LEDController.ALLPOWERFULL_ALIENFX)
			controller = new AlienFXAllPowerFull();
		
		else if(deviceId == LEDController.AREA51_ALIENFX)
			controller = new AlienFXArea51Controller();
		
		else if(deviceId == LEDController.M14XR2_ALIENFX)
			controller = new AlienFXM14xR2Controller();
			
		else if(deviceId == LEDController.M14XR3_ALIENFX)
			controller = new AlienFXM14xR3Controller();

		else if(deviceId == LEDController.M17XR4_ALIENFX)
			controller = new AlienFXM17xController("Alienware M17x R4");

		else if(deviceId == LEDController.M17XR3_ALIENFX)
			controller = new AlienFXM17xController("Alienware M17x R3");

		else if(deviceId == LEDController.M17XR2_ALIENFX)
			controller = new AlienFXM17xController("Alienware M17x R2");

		else if(deviceId == LEDController.M17X_ALIENFX)
			controller = new AlienFXM17xController();

		else if(deviceId == LEDController.M18XR2_ALIENFX)
			controller = new AlienFXM18xR2Controller();

		else throw new AlienFXControllerUnknownDeviceException();

		controller.ping();
		
		return controller;
	}
	
	public synchronized static void deInitialize(){
		controller = null;
		LEDController.destroy();
	}
}
