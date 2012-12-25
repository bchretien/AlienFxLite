package uk.co.progger.alienFXLite.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import uk.co.progger.alienFXLite.alienfx.AlienFXAction;
import uk.co.progger.alienFXLite.alienfx.AlienFXActionBlink;
import uk.co.progger.alienFXLite.alienfx.AlienFXActionColor;
import uk.co.progger.alienFXLite.alienfx.AlienFXActionMorph;
import uk.co.progger.alienFXLite.alienfx.AlienFXProfileSetting;
import uk.co.progger.alienFXLite.led.AlienFXController;
import uk.co.progger.alienFXLite.led.AlienFXRegion;

public class AddActionPanel extends ActionPanel {
	
	private static final long serialVersionUID = 1L;
	private static final ActionHandler actionHandler = new ActionHandler();
	private static final ImageIcon colorIcon = new ImageIcon(AlienFXResources.CREATE_COLOR_ICON_IMAGE);
	private static final ImageIcon blinkIcon = new ImageIcon(AlienFXResources.CREATE_BLINK_ICON_IMAGE);
	private static final ImageIcon morphIcon = new ImageIcon(AlienFXResources.CREATE_MORPH_ICON_IMAGE);
	private static final ImageIcon pasteIcon = new ImageIcon(AlienFXResources.PASTE_ICON_IMAGE);

	private AddButton addColor;
	private AddButton addBlink;
	private AddButton addMorph;
	private AddButton addPaste;
	private DropDownButton button;
	private AlienFXRegion region;
	private AlienFXProfileSetting setting;
	
	public AddActionPanel(ColorModel model, ActionClipboard clipboard, int index) {
		super(model,clipboard,index);
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0,0));

		addColor = new AddButton(this, AddButton.TYPE.COLOR, colorIcon, AlienFXTexts.COLOR_TOOLTIP);
		addBlink = new AddButton(this, AddButton.TYPE.BLINK, blinkIcon, AlienFXTexts.BLINK_TOOLTIP);
		addMorph = new AddButton(this, AddButton.TYPE.MORPH, morphIcon, AlienFXTexts.MORPH_TOOLTIP);
		addPaste = new AddButton(this, AddButton.TYPE.PASTE, pasteIcon, AlienFXTexts.PASTE_TOOLTIP);
		
		addColor.setMargin(AlienFXLiteGUIConstants.buttonInsets);
		addBlink.setMargin(AlienFXLiteGUIConstants.buttonInsets);
		addMorph.setMargin(AlienFXLiteGUIConstants.buttonInsets);
		addPaste.setMargin(AlienFXLiteGUIConstants.buttonInsets);
			
		addColor.addActionListener(actionHandler);
		addBlink.addActionListener(actionHandler);
		addMorph.addActionListener(actionHandler);
		addPaste.addActionListener(actionHandler);
		
		LinkedList<JButton> buttons = new LinkedList<JButton>();
		
		buttons.add(addColor);
		buttons.add(addBlink);
		buttons.add(addMorph);
		buttons.add(addPaste);
		
		button = new DropDownButton(buttons.toArray(new JButton[0]));
		add(button);
	}
	
	public void setSetting(AlienFXProfileSetting setting){
		this.setting = setting;
		setVisible(setting != null);
		button.clearAll();
		if(setting == null)
			return;
		
		region = AlienFXController.regionLookUp.get(setting.getRegion());
		setEnabled(region.maxCommands > setting.getSequence().size());
		
		if(region.canLight)
			button.addButton(0);
		if(region.canBlink)
			button.addButton(1);
		if(region.canMorph)
			button.addButton(2);
		button.addButton(3);
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		button.setEnabled(enabled);
	}
	
	private static class AddButton extends JButton{
		private static final long serialVersionUID = 1L;
		private enum TYPE{COLOR, BLINK, MORPH, PASTE};
		private AddActionPanel panel;
		private TYPE type;

		public AddButton(AddActionPanel panel, TYPE type, Icon icon) {
			super(icon);
			this.panel = panel;
			this.type = type;
		}

		public AddButton(AddActionPanel panel, TYPE type, Icon icon, String tooltip) {
			super(icon);
			this.panel = panel;
			this.type = type;
			this.setToolTipText(tooltip);
		}
	}
	
	private static class ActionHandler implements ActionListener{
		public void actionPerformed(ActionEvent ev) {
			if(ev.getSource() instanceof AddButton){
				AddButton source = (AddButton)ev.getSource();
				if (source.type == AddButton.TYPE.COLOR)
					source.panel.setting.insertIntoSequence(new AlienFXActionColor(source.panel.model.getColor()), source.panel.getIndex());
				else if (source.type == AddButton.TYPE.BLINK)
					source.panel.setting.insertIntoSequence(new AlienFXActionBlink(source.panel.model.getColor()), source.panel.getIndex());
				else if (source.type == AddButton.TYPE.MORPH) {
					source.panel.setting.insertIntoSequence(new AlienFXActionMorph(
									source.panel.getIndex() <= 0 ? source.panel.model.getColor() : source.panel.setting.getSequence().get(source.panel.getIndex()-1).getTrailingColor(), 
									source.panel.getIndex() >= source.panel.setting.getSequence().size() ? Color.BLACK : source.panel.setting.getSequence().get(source.panel.getIndex()).getLeadingColor()), source.panel.getIndex());
				} else if (source.type == AddButton.TYPE.PASTE) {
					List<AlienFXAction> actions = source.panel.clipboard.getClipBoard();
					LinkedList<AlienFXAction> clones = new LinkedList<AlienFXAction>();
					for(AlienFXAction action : actions){
						if(source.panel.setting.getSequence().size() + clones.size() >= AlienFXController.regionLookUp.get(source.panel.setting.getRegion()).maxCommands)
							break;
						if((action.getClass() == AlienFXActionMorph.class && source.panel.region.canMorph) || 
						   (action.getClass() == AlienFXActionColor.class && source.panel.region.canLight) ||  
						   (action.getClass() == AlienFXActionBlink.class && source.panel.region.canBlink))
							clones.add(action.clone());
					}
					if(clones.size() > 0)
						source.panel.setting.insertIntoSequence(clones, source.panel.getIndex());
				}
			}		
		}
	}
}
