package uk.co.progger.alienFXLite.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.progger.alienFXLite.AlienFXProperties;
import uk.co.progger.alienFXLite.alienfx.AlienFXProfile;
import uk.co.progger.alienFXLite.alienfx.AlienFXProfileSetting;
import uk.co.progger.alienFXLite.led.AlienFXController;

public class ProfilePanel extends JPanel{

	private static final long serialVersionUID = 1L;

	private static final String PROFILE = "profile";
	private static final String NO_PROFILE = "noProfile";

	private ProfileModel model;
	private JPanel noProfilePanel;
	private JPanel profilePanel;
	private CardLayout card;
	private ColorModel colorModel;
	private ActionClipboard clipBoard;
	private AlienFXProfile displayingProfile;
	private JSlider speedSlider;
	
	private JPanel speedPanel;
	private JPanel mainPanel;
	private JPanel sidePanel;
	
	private HashMap<String, JPanel> powerModePanels = new HashMap<String, JPanel>();
	private HashMap<AlienFXProfileSetting, RegionPanel> regionPanels = new HashMap<AlienFXProfileSetting, RegionPanel>();
	
	public ProfilePanel(ProfileModel model, ColorModel colorModel){
		init();
		this.model = model;
		this.colorModel = colorModel;
		model.addObserver(new ChangeObserver());
		clipBoard = new ActionClipboard();
		updatePanel();
	}
	
	private void init(){
		card = new CardLayout();
		this.setLayout(card);
		
		noProfilePanel = new JPanel();
		JLabel label = new JLabel(AlienFXTexts.SELECT_PROFILE_TEXT, JLabel.CENTER);
		label.setBorder(BorderFactory.createEmptyBorder(AlienFXLiteGUIConstants.DEFAULT_PAD, AlienFXLiteGUIConstants.DEFAULT_PAD, AlienFXLiteGUIConstants.DEFAULT_PAD, AlienFXLiteGUIConstants.DEFAULT_PAD));
		noProfilePanel.add(label, BorderLayout.CENTER);
		
		Hashtable<Integer, JLabel> dictionary = new Hashtable<Integer, JLabel>();
		dictionary.put(AlienFXController.MIN_SPEED, new JLabel(AlienFXTexts.PROFILE_SPEED_FAST));
		dictionary.put(AlienFXController.MAX_SPEED, new JLabel(AlienFXTexts.PROFILE_SPEED_SLOW));
		
		speedSlider = new JSlider(AlienFXController.MIN_SPEED,AlienFXController.MAX_SPEED);
		speedSlider.setLabelTable(dictionary);
		speedSlider.setPaintLabels(true);
		speedSlider.setInverted(true);
		speedSlider.setSnapToTicks(true);
		speedSlider.setMajorTickSpacing(100);
		speedSlider.setPaintTicks(true);
		speedSlider.addChangeListener(new SliderHandler());
		speedPanel = new JPanel(new BorderLayout());
		speedPanel.setBorder(BorderFactory.createTitledBorder(AlienFXTexts.PROFILE_SPEED_TEXT));
		speedPanel.add(speedSlider, BorderLayout.LINE_START);
		speedSlider.setPreferredSize(new Dimension(400,speedSlider.getPreferredSize().height));
	
		
		JPanel sidePanelHolder = new JPanel(new BorderLayout());
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		sidePanel = new JPanel();
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));
		sidePanelHolder.add(sidePanel, BorderLayout.PAGE_START);
		sidePanelHolder.add(speedPanel, BorderLayout.PAGE_END);
		
		mainPanel.setBorder(BorderFactory.createTitledBorder(AlienFXTexts.DEFAULT_TEXT));
		
		profilePanel = new JPanel();
		profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.PAGE_AXIS));
		profilePanel.add(mainPanel);
		profilePanel.add(sidePanelHolder);
		powerModePanels.put(AlienFXProperties.ALIEN_FX_DEFAULT_POWER_MODE, mainPanel);
		
		add(new JScrollPane(profilePanel), PROFILE);
		add(noProfilePanel, NO_PROFILE);
		
	}
	
	private void updatePanel(){
		
		AlienFXProfile profile = model.getProfile();
		if(profile == null){
			//if there is no profile selected, just display this message
			card.show(this, NO_PROFILE);
			validate();
			return;
		}
		
		if(displayingProfile == profile){
			this.validate();
			this.repaint();
			return;
		}
		
		displayingProfile = profile;
		card.show(this, PROFILE);
		speedSlider.setValue(profile.getSpeed());
		
		int width = getMinLabelWidth();
		for(AlienFXProfileSetting setting : profile.getSettings()){
			RegionPanel region = getRegionPanel(setting);
			region.setSetting(setting, width);
		}
		validate();
		repaint();
	}
	
	private RegionPanel getRegionPanel(AlienFXProfileSetting setting){
		RegionPanel regionPanel = regionPanels.get(setting);
		if(regionPanel == null){
			JPanel panel;
			panel = powerModePanels.get(setting.getPowermode());
			if(panel == null){
				panel= new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
				panel.setBorder(BorderFactory.createTitledBorder(AlienFXController.powermodeLookUp.get(setting.getPowermode()).description));
				powerModePanels.put(setting.getPowermode(), panel);
				sidePanel.add(panel);
			}
			regionPanel = new RegionPanel(colorModel,clipBoard);
			regionPanels.put(setting, regionPanel);
			panel.add(regionPanel);
		}
		return regionPanel;
	}
	
	private int getMinLabelWidth(){
		AlienFXProfile profile = model.getProfile();
		
		String longest = "";
		for(AlienFXProfileSetting setting : profile.getSettings()){
			String desc = AlienFXController.regionLookUp.get(setting.getRegion()).description;
			if(longest.length() < desc.length())
				longest = desc;
		}
		return new JLabel(longest).getPreferredSize().width + AlienFXLiteGUIConstants.DEFAULT_PAD;
	}
	
	private class SliderHandler implements ChangeListener{
		public void stateChanged(ChangeEvent e) {
		    JSlider source = (JSlider)e.getSource();
		    if (!source.getValueIsAdjusting()) {
		        int speed = source.getValue();
		        if(model.getProfile() != null)
		        	model.getProfile().setSpeed(speed);
		    }
		}
	}

	
	private class ChangeObserver implements Observer{
		public void update(Observable arg0, Object arg1) {
			updatePanel();
		}	
	}
}
