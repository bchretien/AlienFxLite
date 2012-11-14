package uk.co.progger.alienFXLite.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import uk.co.progger.alienFXLite.alienfx.AlienFXAction;
import uk.co.progger.alienFXLite.alienfx.AlienFXProfileSetting;

public class ActionClipboard extends Observable{
	private ArrayList<AlienFXAction> stored = new ArrayList<AlienFXAction>(30);
	private AlienFXProfileSetting setting;
	
	public void setSetting(AlienFXProfileSetting setting){
		if(setting != null && this.setting != setting){
			stored.clear();
			setChanged();
			notifyObservers();
		}
		this.setting = setting;
	}
	
	public void addToClipboard(AlienFXAction action, int index){
		while(stored.size() <= index)
			stored.add(null);
		
		if(!stored.contains(action))
			stored.add(index, action);
		
		setChanged();
		notifyObservers();
	}
	
	public boolean contains(AlienFXAction action){
		return stored.contains(action);
	}
	
	public List<AlienFXAction> getClipBoard(){
		LinkedList<AlienFXAction> actions = new LinkedList<AlienFXAction>();
		for(AlienFXAction a : stored){
			if(a != null)
				actions.add(a);
		}
		return actions;
	}

	public void removeFromClipBoard(AlienFXAction action) {
		stored.remove(action);
		setChanged();
		notifyObservers();
	}
}
