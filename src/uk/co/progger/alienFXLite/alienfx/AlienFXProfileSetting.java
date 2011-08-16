package uk.co.progger.alienFXLite.alienfx;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

public class AlienFXProfileSetting extends Observable implements Serializable{

	private static final long serialVersionUID = 1L;
	private String region;
	private String powermode;
	private LinkedList<AlienFXAction> sequence;
	
	private transient ChangeObserver observer = new ChangeObserver();
	
	public AlienFXProfileSetting(String region, String powermode) {
		super();
		this.region = region;
		this.powermode = powermode;
		sequence = new LinkedList<AlienFXAction>();
	}

	public String getRegion() {
		return region;
	}

	public String getPowermode() {
		return powermode;
	}

	public LinkedList<AlienFXAction> getSequence() {
		return new LinkedList<AlienFXAction>(sequence);
	}
	
	public void loaded(){
		if(observer == null)
			observer = new ChangeObserver();
		
		for(AlienFXAction s : sequence){
			s.addObserver(observer);
			s.loaded();
		}
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((powermode == null) ? 0 : powermode.hashCode());
		result = prime * result + ((region == null) ? 0 : region.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlienFXProfileSetting other = (AlienFXProfileSetting) obj;
		if (powermode == null) {
			if (other.powermode != null)
				return false;
		} else if (!powermode.equals(other.powermode))
			return false;
		if (region == null) {
			if (other.region != null)
				return false;
		} else if (!region.equals(other.region))
			return false;
		return true;
	}

	public void insertIntoSequence(AlienFXAction action, int index){
		sequence.add(index, action);
		setChanged();
		action.addObserver(observer);
		notifyObservers(this);
	}
	
	public void insertIntoSequence(LinkedList<AlienFXAction> actions, int index){
		int i=index;
		for(AlienFXAction a : actions){
			sequence.add(i, a);
			a.addObserver(observer);
			i++;
		}
		
		setChanged();
		notifyObservers(this);
	}
	
	public void appendToSequence(AlienFXAction action){
		sequence.add(action);
		setChanged();
		action.addObserver(observer);
		notifyObservers(this);
	}
	
	public void removeFromSequence(AlienFXAction action){
		sequence.remove(action);
		setChanged();
		action.deleteObserver(observer);
		notifyObservers(this);
	}
	
	public AlienFXProfileSetting clone(){
		AlienFXProfileSetting setting = new AlienFXProfileSetting(region, powermode);
		for(AlienFXAction a : sequence)
			setting.sequence.add(a.clone());
		return setting;
	}
	
	private class ChangeObserver implements Observer, Serializable{
		private static final long serialVersionUID = 1L;

		public void update(Observable source, Object sourcePropagated) {
			setChanged();
			notifyObservers(sourcePropagated);
		}
	}
}
