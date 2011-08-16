package uk.co.progger.alienFXLite.alienfx;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import uk.co.progger.alienFXLite.led.AlienFXController;
import uk.co.progger.alienFXLite.led.AlienFXPowerMode;
import uk.co.progger.alienFXLite.led.AlienFXRegion;

public class AlienFXProfile extends Observable implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	//the name of the profile
	private String name;
	//the device id for the profile
	private int deviceId;	
	
	//the speed of the profile
	private int speed;
	
	//the settings for the profile
	private LinkedList<AlienFXProfileSetting> settings;
	
	private transient ChangeObserver observer = new ChangeObserver();
	
	public AlienFXProfile(String name, AlienFXController controller){
		deviceId = controller.deviceId();
		this.name = name;
		
		settings = new LinkedList<AlienFXProfileSetting>();
		
		for(AlienFXRegion region : controller.getAlienFxRegions()){
			for(AlienFXPowerMode p : region.supportedModes){
				AlienFXProfileSetting setting = new AlienFXProfileSetting(region.name, p.name);
				settings.add(setting);
				setting.addObserver(observer);
			}
		}
	}
	
	public AlienFXProfile(String name, AlienFXProfile profile){
		deviceId = profile.deviceId;
		this.name = name;
		
		settings = new LinkedList<AlienFXProfileSetting>();
		
		speed = profile.speed;
		for(AlienFXProfileSetting s : profile.settings){
			AlienFXProfileSetting setting = s.clone();
			setting.addObserver(observer);
			settings.add(setting);
		}
	}
	
	public void loaded(){
		if(observer == null)
			observer = new ChangeObserver();
		for(AlienFXProfileSetting s : settings){
			s.addObserver(observer);
			s.loaded();
		}
	}
	
	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public LinkedList<AlienFXProfileSetting> getSettings() {
		return new LinkedList<AlienFXProfileSetting>(settings);
	}
	
	private class ChangeObserver implements Observer, Serializable{
		private static final long serialVersionUID = 1L;
		public void update(Observable source, Object sourcePropagated) {
			setChanged();
			notifyObservers(sourcePropagated);
		}		
	}
}
