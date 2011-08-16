package uk.co.progger.alienFXLite.gui;

import java.util.Observable;
import java.util.Observer;

import uk.co.progger.alienFXLite.alienfx.AlienFXProfile;

public class ProfileModel extends Observable{
	
	private AlienFXProfile currentProfile;
	private ChangeObserver observer = new ChangeObserver();
	
	public void setProfile(AlienFXProfile profile){
		if(currentProfile != null)
			currentProfile.deleteObserver(observer);
		
		currentProfile = profile;
		if(currentProfile != null)
			currentProfile.addObserver(observer);
		
		setChanged();
		notifyObservers(this);
	}
		
	public AlienFXProfile getProfile() {
		return currentProfile;
	}

	private class ChangeObserver implements Observer{
		public void update(Observable source, Object sourcePropagated) {
			setChanged();
			notifyObservers(sourcePropagated);
		}		
	}
}
