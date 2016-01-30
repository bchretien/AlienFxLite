package uk.co.progger.alienFXLite.gui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import uk.co.progger.alienFXLite.AlienFXProperties;
import uk.co.progger.alienFXLite.alienfx.AlienFXEngine;
import uk.co.progger.alienFXLite.alienfx.AlienFXProfile;
import uk.co.progger.alienFXLite.alienfx.AlienFXProfiles;
import uk.co.progger.alienFXLite.led.AlienFXCommunicationException;

public class ProfileSelectionPanel extends JPanel {
	
	

	private static final long serialVersionUID = 1L;

	private JComboBox<AlienFXProfile> chooseProfileBox;
	private JButton newButton;
	private JButton saveButton;
	private JButton reloadButton;
	private JButton deleteButton;
	private JButton applyButton;
	private AlienFXEngine engine;
	private ProfileModel model;
	private AlienFXProfiles profiles;
	
	public ProfileSelectionPanel(ProfileModel model, AlienFXEngine engine, AlienFXProfiles profiles){
		this.engine = engine;
		this.model = model;
		this.profiles = profiles;
		
		//load images
		chooseProfileBox = new JComboBox<AlienFXProfile>(profiles);

		newButton = new JButton(new ImageIcon(AlienFXResources.NEW_PROFILE_ICON_IMAGE));
		newButton.setToolTipText(AlienFXTexts.CREATE_A_NEW_PROFILE);
		newButton.addActionListener(new NewLister());
		
		saveButton = new JButton(new ImageIcon(AlienFXResources.SAVE_PROFILE_ICON_IMAGE));
		saveButton.setToolTipText(AlienFXTexts.SAVE_THE_CURRENT_PROFILE);
		saveButton.addActionListener(new SaveListener());
		
		reloadButton = new JButton(new ImageIcon(AlienFXResources.RELOAD_PROFILE_ICON_IMAGE));
		reloadButton.setToolTipText(AlienFXTexts.RELOAD_THE_CURRENT_PROFILE);
		reloadButton.addActionListener(new ReloadListener());
		
		deleteButton = new JButton(new ImageIcon(AlienFXResources.ERASE_PROFILE_ICON_IMAGE));
		deleteButton.setToolTipText(AlienFXTexts.DELETE_THE_CURRENT_PROFILE);
		deleteButton.addActionListener(new DeleteListener());
		
		applyButton = new JButton(new ImageIcon(AlienFXResources.APPLY_PROFILE_ICON_IMAGE));
		applyButton.setToolTipText(AlienFXTexts.APPLY_THE_CURRENT_PROFILE);
		applyButton.addActionListener(new ApplyListener());

		model.addObserver(new ModelObserver());
		updateButtons();
		init();
	}
	
	private void init(){
		SpringLayout layout = new SpringLayout();
		this.setLayout(layout);
		
		layout.putConstraint(SpringLayout.SOUTH, this, AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.SOUTH,applyButton );
		layout.putConstraint(SpringLayout.EAST, applyButton, -AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.NORTH, applyButton, AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.NORTH, this);
		
		layout.putConstraint(SpringLayout.EAST, deleteButton, -AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.WEST, reloadButton);
		layout.putConstraint(SpringLayout.NORTH, deleteButton, AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.NORTH, this);
		
		layout.putConstraint(SpringLayout.EAST, reloadButton, -AlienFXLiteGUIConstants.DEFAULT_PAD*4, SpringLayout.WEST, applyButton);
		layout.putConstraint(SpringLayout.NORTH, reloadButton, AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.NORTH, this);
		
		layout.putConstraint(SpringLayout.EAST, saveButton, -AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.WEST, deleteButton);
		layout.putConstraint(SpringLayout.NORTH, saveButton, AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.NORTH, this);
		
		layout.putConstraint(SpringLayout.EAST, newButton, -AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.WEST, saveButton);
		layout.putConstraint(SpringLayout.NORTH, newButton, AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.NORTH, this);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, chooseProfileBox, 0, SpringLayout.VERTICAL_CENTER, saveButton);
		layout.putConstraint(SpringLayout.EAST, chooseProfileBox, -100, SpringLayout.WEST, newButton);
		layout.putConstraint(SpringLayout.WEST, chooseProfileBox, 260, SpringLayout.WEST, this);
		
		newButton.setMargin(new Insets(AlienFXLiteGUIConstants.BUTTON_INSET, AlienFXLiteGUIConstants.BUTTON_INSET, AlienFXLiteGUIConstants.BUTTON_INSET, AlienFXLiteGUIConstants.BUTTON_INSET));
		saveButton.setMargin(new Insets(AlienFXLiteGUIConstants.BUTTON_INSET, AlienFXLiteGUIConstants.BUTTON_INSET, AlienFXLiteGUIConstants.BUTTON_INSET, AlienFXLiteGUIConstants.BUTTON_INSET));
		reloadButton.setMargin(new Insets(AlienFXLiteGUIConstants.BUTTON_INSET, AlienFXLiteGUIConstants.BUTTON_INSET, AlienFXLiteGUIConstants.BUTTON_INSET, AlienFXLiteGUIConstants.BUTTON_INSET));
		deleteButton.setMargin(new Insets(AlienFXLiteGUIConstants.BUTTON_INSET, AlienFXLiteGUIConstants.BUTTON_INSET, AlienFXLiteGUIConstants.BUTTON_INSET, AlienFXLiteGUIConstants.BUTTON_INSET));
		applyButton.setMargin(new Insets(AlienFXLiteGUIConstants.BUTTON_INSET, AlienFXLiteGUIConstants.BUTTON_INSET, AlienFXLiteGUIConstants.BUTTON_INSET, AlienFXLiteGUIConstants.BUTTON_INSET));

		add(chooseProfileBox);
		add(newButton);
		add(saveButton);
		add(reloadButton);
		add(deleteButton);
		add(applyButton);
	}
	
	private void updateButtons(){
		applyButton.setEnabled(model.getProfile() != null);
		deleteButton.setEnabled(model.getProfile() != null);
		saveButton.setEnabled(model.getProfile() != null);
		reloadButton.setEnabled(model.getProfile() != null);
	}
	
	private class NewLister implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String name = JOptionPane.showInputDialog(ProfileSelectionPanel.this.getRootPane(), AlienFXTexts.ENTER_NAME_TEXT, AlienFXTexts.ENTER_NAME_TITLE_TEXT, JOptionPane.QUESTION_MESSAGE);
			if(name == null)
				return;
			if(name.isEmpty()){
				JOptionPane.showMessageDialog(ProfileSelectionPanel.this.getRootPane(), AlienFXTexts.PROFILE_NAME_EMPTY_ERROR_TEXT, AlienFXTexts.ALIEN_FX_ERROR_TITLE_TEXT, JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			//check if there is a profile like this present. An iteration will be fast enough. 
			//I doubt anyone will have more that 100 profiles.
			//TODO: optimize?
			for(AlienFXProfile p : profiles.getProfiles()){
				if(name.equalsIgnoreCase(p.getName())){
					JOptionPane.showMessageDialog(ProfileSelectionPanel.this.getRootPane(), AlienFXTexts.PROFILE_EXISTS_ERROR_TEXT, AlienFXTexts.ALIEN_FX_ERROR_TITLE_TEXT, JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			
			AlienFXProfile newProfile;
			if(model.getProfile() == null)
				newProfile = engine.createProfile(name);
			else
				newProfile = new AlienFXProfile(name, model.getProfile());
			
			profiles.addProfile(newProfile);
			profiles.setSelectedItem(newProfile);
		}
	}
	
	private class SaveListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			int choice = JOptionPane.showConfirmDialog(null,
					"Are you sure you want to save this profile?", "Save profile",
					JOptionPane.YES_NO_OPTION);
			if (choice == JOptionPane.YES_OPTION)
			{
				try {
					profiles.writeProfile(model.getProfile());
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(ProfileSelectionPanel.this.getRootPane(), String.format(AlienFXTexts.SAVE_PROFILE_ERROR_FORMAT,e1.getMessage()), AlienFXTexts.ALIEN_FX_ERROR_TITLE_TEXT, JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private class ReloadListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			// We basically delete the modified AlienFXProfile corresponding
			// to the current profile, and we reload it from the saved file.
			AlienFXProfile modified_profile = model.getProfile();
			File f = new File(AlienFXProperties.ALIEN_FX_PROFILE_FOLDER_PATH
					+ modified_profile.getName()
					+ AlienFXProperties.ALIEN_FX_PROFILE_EXTENSION);
			profiles.removeProfileFromList(modified_profile);
			AlienFXProfile original_profile = profiles.loadProfile(f);
			model.setProfile(original_profile);
		}
	}
	
	private class DeleteListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			int choice = JOptionPane.showConfirmDialog(null,
					"Are you sure you want to delete this profile?", "Delete profile",
					JOptionPane.YES_NO_OPTION);
			if (choice == JOptionPane.YES_OPTION)
				profiles.removeProfile(model.getProfile());
		}
	}
	
	private class ApplyListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			try {
				engine.applyProfile(model.getProfile());
			} catch (AlienFXCommunicationException e1) {
				JOptionPane.showMessageDialog(ProfileSelectionPanel.this.getRootPane(), String.format(AlienFXTexts.COMMUNICATION_ERROR_FORMAT,e1.getMessage()), AlienFXTexts.ALIEN_FX_ERROR_TITLE_TEXT, JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private class ModelObserver implements Observer{
		public void update(Observable arg0, Object arg1) {
			updateButtons();
		}		
	}
}
