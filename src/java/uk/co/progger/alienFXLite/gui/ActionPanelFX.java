package uk.co.progger.alienFXLite.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import uk.co.progger.alienFXLite.alienfx.AlienFXAction;
import uk.co.progger.alienFXLite.alienfx.AlienFXActionBlink;
import uk.co.progger.alienFXLite.alienfx.AlienFXActionColor;
import uk.co.progger.alienFXLite.alienfx.AlienFXActionMorph;
import uk.co.progger.alienFXLite.alienfx.AlienFXProfileSetting;

public class ActionPanelFX extends ActionPanel{

	private static final long serialVersionUID = 1L;
	private static int REMOVE_BUTTON_WIDTH = 26;
	
	private AlienFXAction action;
	private AlienFXProfileSetting setting;
	private ColorPanel colorPanel;
	private BlinkPanel blinkPanel;
	private MorphPanel morphPanel;
	private CardLayout card;
	private JPanel actionPanel;
	
	private enum ActionType {COLOR, BLINK, MORPH};

	public ActionPanelFX(ColorModel model, ActionClipboard clipboard, int index) {
		super(model, clipboard, index);
		this.setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
		this.setBorder(AlienFXLiteGUIConstants.regularBorder);
		this.setPreferredSize(AlienFXLiteGUIConstants.actionPanelFXDimension);
	
		if(clipboard != null)
			clipboard.addObserver(new ClipBoardObserver());
		
		actionPanel = new JPanel();
		card = new CardLayout();
		actionPanel.setLayout(card);
		actionPanel.add(colorPanel = new ColorPanel(), AlienFXActionColor.class.toString());
		actionPanel.add(blinkPanel = new BlinkPanel(), AlienFXActionBlink.class.toString());
		actionPanel.add(morphPanel = new MorphPanel(), AlienFXActionMorph.class.toString());
		card.show(actionPanel, AlienFXActionColor.class.toString());
		
		add(actionPanel);
		JButton remove_button = new RemoveButton();
		remove_button.setPreferredSize(new Dimension(REMOVE_BUTTON_WIDTH, REMOVE_BUTTON_WIDTH));
		add(remove_button);
	}
	
	public void setAction(AlienFXProfileSetting settings, AlienFXAction action) {
		if(settings == null || action == null){
			setVisible(false);
			return;
		}
		setVisible(true);
		this.setting = settings;
		this.action = action;
		card.show(actionPanel, action.getClass().toString());
		
		if(action.getClass() == AlienFXActionColor.class)
			colorPanel.setAction(action);
		if(action.getClass() == AlienFXActionBlink.class)
			blinkPanel.setAction(action);
		if(action.getClass() == AlienFXActionMorph.class)
			morphPanel.setAction(action);
		
		updateSelection();
	}
	
	private void updateSelection(){
		if(clipboard.contains(action))
			setBorder(AlienFXLiteGUIConstants.selectedBorder);
		else
			setBorder(AlienFXLiteGUIConstants.regularBorder);
	}
	
	public AlienFXAction getAction(){
		return action;
	}
	
	private class ClipBoardObserver implements Observer{
		public void update(Observable o, Object arg) {
			updateSelection();
		}		
	}
	
	private class BlinkPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		private ColorModelPanel color;
		public BlinkPanel(){
			super(new BorderLayout());
			color = new ColorModelPanel(ActionType.BLINK);
			add(color, BorderLayout.CENTER);
		}
		public void setAction(AlienFXAction action){
			color.setColorModel(action.getLeadingColorModel());
		}
	}
	
	private class ColorPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		private ColorModelPanel color;
		public ColorPanel(){
			super(new BorderLayout());
			color = new ColorModelPanel(ActionType.COLOR);
			add(color, BorderLayout.CENTER);
		}
		public void setAction(AlienFXAction action){
			color.setColorModel(action.getLeadingColorModel());
		}
	}
	
	private class MorphPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		private ColorModelPanel color1;
		private ColorModelPanel color2;
		public MorphPanel(){
			super(new GridLayout(1, 2));
			color1 = new ColorModelPanel(ActionType.MORPH);
			this.add(color1);
			color2 = new ColorModelPanel(ActionType.MORPH);
			this.add(color2);
		}
		public void setAction(AlienFXAction action){
			color1.setColorModel(action.getLeadingColorModel());
			color2.setColorModel(action.getTrailingColorModel());
		}
	}
	
	private class RemoveButton extends JButton implements ActionListener{
		private static final long serialVersionUID = 1L;
		public RemoveButton(){
			super(AlienFXLiteGUIConstants.removeButtonImageIcon);
			this.setMargin(AlienFXLiteGUIConstants.buttonInsets);
			this.setMaximumSize(AlienFXLiteGUIConstants.maxDimension);
			this.addActionListener(this);
		}
		public void actionPerformed(ActionEvent e) {
			setting.removeFromSequence(action);
		}
	}
	
	private class ColorModelPanel extends JButton implements ActionListener, Observer, MouseListener
	{
		private static final long serialVersionUID = 1L;
		private ColorModel cModel;
		private ActionType type;

		public ColorModelPanel(ActionType type) {
			super();
			this.type = type;
			this.addActionListener(this);
			this.addMouseListener(this);
		}
		public void paint(Graphics g1) {
			Graphics2D g = (Graphics2D) g1;
			g.setStroke(AlienFXLiteGUIConstants.borderStroke);
			g.setColor(cModel.getColor());
			if(type == ActionType.BLINK){
				int height = getHeight();
				int width = getWidth();
				for(int i = 3; i < width-6; i++)
					g.fillOval(i, (int)(Math.sin(1+i*(Math.PI/9.3))*(height - 15)) + 9, 4, 4);
			}
			else if (type == ActionType.COLOR || type == ActionType.MORPH)
			{
				g.fillRoundRect(2, 0, getWidth()-4, getHeight()-0,16,16);
			}

			//draw border
			g.setColor(AlienFXLiteGUIConstants.regularBorderColor);
			g.drawRoundRect(2, 0, getWidth()-4, getHeight()-0,16,16);
		}
		
		public void setColorModel(ColorModel cModel){
			if(this.cModel != null)
				this.cModel.deleteObserver(this);
			this.cModel = cModel;
			if(this.cModel != null)
				this.cModel.addObserver(this);
		}
		public void actionPerformed(ActionEvent ae) {
			if(ae.getSource() == this)
				cModel.setColor(ActionPanelFX.this.model.getColor(), ActionPanelFX.this);
		}
		public void update(Observable arg0, Object arg1) {
			repaint();
		}
		protected void finalize() throws Throwable {
			super.finalize();
			cModel.deleteObserver(this);
		}
		public void mouseClicked(MouseEvent e) {
			if(e.getSource() == this){
				if(e.getButton() == MouseEvent.BUTTON3)
					ActionPanelFX.this.model.setColor(cModel.getColor(),ActionPanelFX.this);
			}
		}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
	}
}
