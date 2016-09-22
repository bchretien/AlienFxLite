package uk.co.progger.alienFXLite.alienfx;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import uk.co.progger.alienFXLite.AlienFXProperties;
import uk.co.progger.alienFXLite.led.AlienFXCommunicationException;
import uk.co.progger.alienFXLite.led.AlienFXController;
import uk.co.progger.alienFXLite.led.AlienFXControllerFactory;
import uk.co.progger.alienFXLite.led.AlienFXControllerNotFoundException;
import uk.co.progger.alienFXLite.led.AlienFXControllerUnknownDeviceException;
import uk.co.progger.alienFXLite.led.AlienFXPowerMode;
import uk.co.progger.alienFXLite.led.AlienFXRegion;

/**
 * The alienFXEngine class contains the logic on how to set a given profile onto the AlienFX LED controller
 */
public class AlienFXEngine {

	//The controller
	private AlienFXController controller;
	
	public AlienFXEngine() throws AlienFXControllerNotFoundException, AlienFXControllerUnknownDeviceException, AlienFXCommunicationException{
		controller = AlienFXControllerFactory.getAlienFXDevice();
	}
	
	public void reset() throws AlienFXCommunicationException {
		controller.reset(controller.RESET_ALL_LIGHTS_OFF);
	}
	
	/**
	 * Applies a given profile. This communicates with the AlienFX device.
	 * @param p - the profile to apply
	 * @throws AlienFXCommunicationException - thrown when there is an error in communicating with the device
	 */
	public void applyProfile(AlienFXProfile p) throws AlienFXCommunicationException{
		ConcurrentHashMap<String, AlienFXRegion> regionLookUp = AlienFXController.regionLookUp;
		ConcurrentHashMap<String, AlienFXPowerMode> powermodeLookUp = AlienFXController.powermodeLookUp;;
		
		HashMap<String, LinkedList<AlienFXProfileSetting>> settingsMap = new HashMap<String, LinkedList<AlienFXProfileSetting>>();
		for(AlienFXProfileSetting s : p.getSettings()){
			LinkedList<AlienFXProfileSetting> settings = settingsMap.get(s.getPowermode());
			if(settings == null){
				settings = new LinkedList<AlienFXProfileSetting>();
				settingsMap.put(s.getPowermode(), settings);
			}
			settings.add(s);
		}
		
		
		int index =1;
		//load the data
		for(int run = 0; run < 2; run++){
			int commandsWritten = 0;
			for(String key : settingsMap.keySet()){
				if(run == 1 && !key.equals(AlienFXProperties.ALIEN_FX_DEFAULT_POWER_MODE))
					continue;
				
				if(commandsWritten > 0){
					controller.reset(controller.RESET_ALL_LIGHTS_ON);
					controller.waitForReady();
				}
				
				if(run == 1)
					controller.setSpeed(p.getSpeed());
				
				commandsWritten = 0;
				for(AlienFXProfileSetting s : settingsMap.get(key)){
				//wait until the controller is ready
					for(AlienFXAction a : s.getSequence()){
						
						if(run == 0)
							controller.saveNextInstruction(powermodeLookUp.get(s.getPowermode()).blockId);
						
						if(a.getClass() == AlienFXActionColor.class)
							controller.setLightWithColor(index, regionLookUp.get(s.getRegion()).regionId, ((AlienFXActionColor) a).getColor());
						
						if(a.getClass() == AlienFXActionBlink.class)
							controller.setBlinkLightWithColor(index, regionLookUp.get(s.getRegion()).regionId, ((AlienFXActionBlink) a).getColor());
						
						if(a.getClass() == AlienFXActionMorph.class)
							controller.setMorphLightWithColor(index, regionLookUp.get(s.getRegion()).regionId, ((AlienFXActionMorph) a).getColor(), ((AlienFXActionMorph) a).getMorphToColor());
						
						commandsWritten++;
					}
					index++;
					if(s.getSequence().size() > 0){
						if(run == 0)
							controller.saveNextInstruction(powermodeLookUp.get(s.getPowermode()).blockId);
						controller.finishLoop();
					}
				}
				
				if(run == 0 && commandsWritten > 0)
					controller.finishSave();
			}
		}
		controller.transmitAndExecute();
	}
	
	/**
	 * Should be called once the application is being closed
	 */
	public void shutDown() {
		AlienFXControllerFactory.deInitialize();
	}
	
	/**
	 * Checks if a given profile can be loaded onto the current AlienFX device. Not implemented.
	 * @param p - the profile to verify
	 * @return true if profile is correct and can be loaded onto the system. False otherwise
	 */
	public boolean verifyProfile(AlienFXProfile p){
		return true;
	}

	/**
	 * Creates a profile for the current alienFX controller
	 * @param name - the name of the profile
	 * @param c - the default color
	 * @return a new profile
	 */
	public AlienFXProfile createProfile(String name) {
		return new AlienFXProfile(name, controller);
	}
	
	/**
	 * Return the controller associated with the engine
	 * @return AlienFX controller
	 */
	public AlienFXController getController()
	{
		return controller;
	}
}
