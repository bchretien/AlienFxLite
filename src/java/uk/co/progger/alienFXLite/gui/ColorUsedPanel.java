package uk.co.progger.alienFXLite.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import uk.co.progger.alienFXLite.alienfx.AlienFXAction;
import uk.co.progger.alienFXLite.alienfx.AlienFXProfile;
import uk.co.progger.alienFXLite.alienfx.AlienFXProfileSetting;

public class ColorUsedPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private static final int BUTTON_SIZE = 30;
	
	private ProfileModel profileModel;
	private ColorModel model;
	private JPanel container;
	private JScrollPane pane;
	private ArrayList<ColorButton> buttons;
	
	private static final Dimension colorButtonDimension = new Dimension(BUTTON_SIZE,BUTTON_SIZE);
	
	public ColorUsedPanel(ProfileModel profileModel, ColorModel model) {
		super(new BorderLayout());
		this.profileModel = profileModel;
		this.model = model;
		container = new JPanel(new FlowLayout(FlowLayout.LEADING, AlienFXLiteGUIConstants.DEFAULT_PAD,AlienFXLiteGUIConstants.DEFAULT_PAD));

		setBorder(BorderFactory.createTitledBorder(AlienFXTexts.COLORS_PROFILE_TITLE));
		pane = new JScrollPane(container, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(pane, BorderLayout.CENTER);
		
		profileModel.addObserver(new ProfileObserver());
		buttons = new ArrayList<ColorButton>();
		update();
	}
	
	private void update(){
		
		clearButtons();
		//find all the unique colors
		int buttons = 0;
		Set<Color> colors = new HashSet<Color>();
		AlienFXProfile profile = profileModel.getProfile();
		if(profile != null){
			for(AlienFXProfileSetting s : profile.getSettings()){
				for(AlienFXAction a : s.getSequence()){
					Color c1 = a.getLeadingColor();
					Color c2 = a.getTrailingColor();
					
					if(!colors.contains(c1))
						setButtonColor(c1, buttons++);
					colors.add(c1);
					if(!colors.contains(c2))
						setButtonColor(c2, buttons++);
					colors.add(c2);
				}
			}
		}
		colors.clear();
		//calculate the maximum number of rows:
		if(pane.getViewport().getWidth() != 0){
			int width = pane.getViewport().getWidth() - pane.getVerticalScrollBar().getSize().width - AlienFXLiteGUIConstants.DEFAULT_PAD*3;
			int rows = (int)Math.ceil(buttons * (BUTTON_SIZE+AlienFXLiteGUIConstants.DEFAULT_PAD*2) / (double)(width));
			int hsize = rows *  (BUTTON_SIZE+AlienFXLiteGUIConstants.DEFAULT_PAD*2);
			container.setPreferredSize(new Dimension( width, hsize));
		}
		container.validate();
		pane.validate();
		this.validate();
		this.repaint();
	}
	
	private void clearButtons(){
		for(ColorButton b : buttons){
			if(b == null)
				continue;
			b.setVisible(false);
		}
	}
	
	private void setButtonColor(Color c, int index){
		while(buttons.size() <= index)
			buttons.add(null);
		
		ColorButton b = buttons.get(index);
		if(b == null){
			b = new ColorButton(c);
			buttons.set(index, b);
			container.add(b, index);
		}
		b.setColor(c);
		b.setVisible(true);
	}
	
	private class ProfileObserver implements Observer{
		public void update(Observable o, Object arg) {
			ColorUsedPanel.this.update();
		}		
	}
	
	private class ColorButton extends JButton implements MouseListener{
		private static final long serialVersionUID = 1L;
		private Color c;
		public ColorButton(Color c){
			this.c = c;
			this.setPreferredSize(colorButtonDimension);
			this.addMouseListener(this);
		}
		public void setColor(Color c){
			this.c = c;
			repaint();
		}
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(c);
			g.fillRect(5, 5, getWidth()-10, getHeight()-10);
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getButton() == MouseEvent.BUTTON3)
				ColorUsedPanel.this.model.setColor(c);
			else if(e.getButton() == MouseEvent.BUTTON1){
				AlienFXProfile profile = profileModel.getProfile();
				Color changedColor = c;
				if(profile != null){
					for(AlienFXProfileSetting s : profile.getSettings()){
						for(AlienFXAction a : s.getSequence()){
							if(a.getLeadingColor().getRGB() == changedColor.getRGB())
								a.getLeadingColorModel().setColor(ColorUsedPanel.this.model.getColor());
							
							if(a.getTrailingColor().getRGB() == changedColor.getRGB())
								a.getTrailingColorModel().setColor(ColorUsedPanel.this.model.getColor());
						}
					}
				}
			}
		}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
	}
}
