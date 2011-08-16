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
	public synchronized static AlienFXController getAlienFXDevice() throws AlienFXControllerNotFoundException, AlienFXCommunicationException{
		if(controller != null)
			return controller;
		
		int deviceId = LEDController.initialize();
		
		if(deviceId == LEDController.NOT_FOUND)
			throw new AlienFXControllerNotFoundException();
		
		if(deviceId == LEDController.ALLPOWERFULL_ALIENFX)
			controller = new AlienFXAllPowerFull();
		
		if(deviceId == LEDController.AREA51_ALIENFX)
			controller = new AlienFXArea51Controller();
		
		controller.ping();
		
		return controller;
	}
	
	public synchronized static void deInitialize(){
		controller = null;
		LEDController.destroy();
	}
}
