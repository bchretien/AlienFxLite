package uk.co.progger.alienFXLite.alienfx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import uk.co.progger.alienFXLite.AlienFXProperties;
import uk.co.progger.alienFXLite.gui.ProfileModel;

public class AlienFXProfiles  implements ComboBoxModel<AlienFXProfile> {

	private static final AlienFXProfile NEW_PROFILE = null;
	private LinkedList<ListDataListener> listeners = new LinkedList<ListDataListener>();
	private LinkedList<AlienFXProfile> profiles;
	private ProfileModel model;
	
	public AlienFXProfiles(ProfileModel model){
		profiles = new LinkedList<AlienFXProfile>();
		this.model = model;
	}
	
	public void loadProfiles(){
		File f = new File(AlienFXProperties.ALIEN_FX_PROFILE_FOLDER_PATH);
		if(f == null)
			return;
		if(!f.isDirectory())
			return;
		for(File pf : f.listFiles()){
			loadProfile(pf);
		}
	}

	public AlienFXProfile loadProfile(File pf){
		if(pf.isFile()){
			if(pf.getName().endsWith(AlienFXProperties.ALIEN_FX_PROFILE_EXTENSION)){
				FileInputStream fin;
				try 
				{
					fin = new FileInputStream(pf);
					
				    ObjectInputStream ois = new ObjectInputStream(fin);
				    AlienFXProfile profile = (AlienFXProfile)ois.readObject();
				    profile.loaded();
				    addProfile(profile);
				    ois.close();
				    return profile;
				} catch (Exception e) {
						e.printStackTrace();
						return null;
				}
			}
		}
		return null;
	}

	public void writeProfile(AlienFXProfile p) throws IOException{
		File f = new File(AlienFXProperties.ALIEN_FX_PROFILE_FOLDER_PATH);
		if(!f.exists())
			f.mkdir();
		FileOutputStream fout = new FileOutputStream(AlienFXProperties.ALIEN_FX_PROFILE_FOLDER_PATH+p.getName()+AlienFXProperties.ALIEN_FX_PROFILE_EXTENSION);
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		oos.writeObject(p);
		oos.close();
	}
	
	public void addProfile(AlienFXProfile p){
		profiles.add(p);
		for(ListDataListener l : new LinkedList<ListDataListener>(listeners))
			l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, profiles.size()));
	}
	
	public void removeProfileFromList(AlienFXProfile p){
		if(p == model.getProfile())
			model.setProfile(null);

		profiles.remove(p);
		for(ListDataListener l : new LinkedList<ListDataListener>(listeners))
			l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, profiles.size()));
	}

	public void removeProfile(AlienFXProfile p){
		removeProfileFromList(p);

		//try to remove the file as well
		File f = new File(AlienFXProperties.ALIEN_FX_PROFILE_FOLDER_PATH+p.getName()+AlienFXProperties.ALIEN_FX_PROFILE_EXTENSION);
		f.delete();
	}
	
	public LinkedList<AlienFXProfile> getProfiles(){
		return new LinkedList<AlienFXProfile>(profiles);
	}

	@Override
	public Object getSelectedItem() {
		if(model.getProfile() == null)
			return NEW_PROFILE;
		return model.getProfile();
	}

	@Override
	public void setSelectedItem(Object anItem) {
		if(anItem instanceof AlienFXProfile)
			model.setProfile((AlienFXProfile)anItem);
		else
			model.setProfile(null);
	}

	@Override
	public void addListDataListener(ListDataListener listener) {
		listeners.add(listener);
	}

	@Override
	public AlienFXProfile getElementAt(int index) {
		if(index == 0)
			return NEW_PROFILE;
		return profiles.get(index-1);
	}

	@Override
	public int getSize() {
		return profiles.size()+1;
	}

	@Override
	public void removeListDataListener(ListDataListener listener) {
		listeners.remove(listener);
	}
}
