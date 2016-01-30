package uk.co.progger.alienFXLite.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import uk.co.progger.alienFXLite.alienfx.AlienFXAction;
import uk.co.progger.alienFXLite.alienfx.AlienFXProfileSetting;
import uk.co.progger.alienFXLite.led.AlienFXController;

public class RegionPanel extends JPanel{	
	private static final int REGION_V_GAP = 4;
	private static final int REGION_H_GAP = 8;

	private static final long serialVersionUID = 1L;
	private static final int ARRAY_INITIAL_CAPACITY = 40;

	private static final Dimension regionPanelDimension = new Dimension(1, new ActionPanelFX(null, null, 0).getPreferredSize().height);
	private static final MouseHandler mousehandler = new MouseHandler();
	private static final Color selectionColor = new JPanel().getBackground().darker().darker();
	
	private static final int MODE_DELETE = 2;
	private static final int MODE_ADD = 1;
	private static final int MODE_NORMAL = 0;
	
	private int mouseInitX,mouseInitY, mouseX, mouseY;
	
	private JPanel sequencePanel;
	private ActionClipboard clipboard;
	private AlienFXProfileSetting setting;
	private ColorModel model;
	private ChangeObserver observer;
	private boolean selecting;
	
	private AddActionPanel[] addPanels = new AddActionPanel[ARRAY_INITIAL_CAPACITY];
	private ActionPanelFX[] fxPanels = new ActionPanelFX[ARRAY_INITIAL_CAPACITY];
	private JLabel label;
	
	public RegionPanel(ColorModel model, ActionClipboard board){
		super(new BorderLayout());
		this.clipboard = board;
		this.model = model;
		this.setBorder(BorderFactory.createEtchedBorder());
		
		label = new JLabel();		
		observer = new ChangeObserver();
		sequencePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, REGION_H_GAP, REGION_V_GAP));

		//needed for selection
		this.addMouseListener(mousehandler);
		this.addMouseMotionListener(mousehandler);
		
		//this little trick will force the panel to stay at a good size
		JPanel panel = new JPanel();
		panel.setPreferredSize(regionPanelDimension);
		sequencePanel.add(panel);
	
		this.add(label,BorderLayout.LINE_START);
		this.add(sequencePanel, BorderLayout.CENTER);
	}
	
	public void setSetting(AlienFXProfileSetting setting, int width){
		if(this.setting != null)
			setting.deleteObserver(observer);
		this.setting = setting;
		if(setting != null)
			setting.addObserver(observer);
		
		if(setting == null){
			setVisible(false);
			return;
		}
		
		label.setText(AlienFXController.regionLookUp.get(setting.getRegion()).description);
		label.setPreferredSize(new Dimension(width, label.getPreferredSize().height));

		update();
	}
	
	public void update(){
		int index = 0;
		clearPanels();
		setAddActionPanel(setting, index);
		
		for(AlienFXAction action : setting.getSequence()){
			setAddActionPanel(setting, index);
			setFXActionPanel(setting, action, index);
			index++;
		}
		setAddActionPanel(setting, index);
		sequencePanel.validate();
		sequencePanel.repaint();
	}
	
	private void clearPanels() {
		for(int i = 0; i < addPanels.length; i++){
			if(addPanels[i] != null)
				addPanels[i].setSetting(null);
		}
		
		for(int i = 0; i < fxPanels.length; i++){
			if(fxPanels[i] != null)
				fxPanels[i].setAction(null, null);
		}
	}

	private AddActionPanel setAddActionPanel(AlienFXProfileSetting settings, int index){
		while(index >= addPanels.length){
			AddActionPanel[] tmp = new AddActionPanel[2*addPanels.length];
			for(int i=0; i < addPanels.length; i++)
				tmp[i] = addPanels[i];
			addPanels = tmp;
		}
		
		if(addPanels[index] == null){
			addPanels[index] = new AddActionPanel(model, clipboard, index);
			sequencePanel.add(addPanels[index], 2*index);
		}
		
		addPanels[index].setSetting(settings);
		return addPanels[index];
	}
	
	private ActionPanelFX setFXActionPanel(AlienFXProfileSetting settings, AlienFXAction action, int index){
		while(index >= fxPanels.length){
			ActionPanelFX[] tmp = new ActionPanelFX[2*fxPanels.length];
			for(int i=0; i < fxPanels.length; i++)
				tmp[i] = fxPanels[i];
			fxPanels = tmp;
		}
		
		if(fxPanels[index] == null){
			fxPanels[index] = new ActionPanelFX(model, clipboard, index);
			sequencePanel.add(fxPanels[index], 2*index + 1);
		}
		
		fxPanels[index].setAction(settings, action);
		return fxPanels[index];
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if(selecting){
			
			int x1 = Math.min(mouseX, mouseInitX);
			int y1 = Math.min(mouseY, mouseInitY);
			int x2 = Math.max(mouseX, mouseInitX);
			int y2 = Math.max(mouseY, mouseInitY);
			g.setColor(selectionColor);
			
			g.drawRect(x1, y1, x2-x1, y2-y1);
		}
	}
	
	private void doSelection(int mode){
		clipboard.setSetting(setting);
		int mouseX1 = Math.min(mouseX, mouseInitX) - sequencePanel.getX();
		int mouseX2 = Math.max(mouseX, mouseInitX) - sequencePanel.getX();

		for(Component c : sequencePanel.getComponents()){
			if(c instanceof ActionPanelFX){
				ActionPanelFX panel = (ActionPanelFX)c;
				int x1 = panel.getX();
				int x2 = x1 + panel.getWidth();
				
				if(mouseX1 < x1 && mouseX2 > x1 || mouseX1 < x2 && mouseX2 > x2){
					if(mode == MODE_DELETE)
						clipboard.removeFromClipBoard(panel.getAction());
					else
						clipboard.addToClipboard(panel.getAction(), panel.getIndex());
				}else if(mode == MODE_NORMAL)
					clipboard.removeFromClipBoard(panel.getAction());
			}
		}
	}
	
	private class ChangeObserver implements Observer{
		public void update(Observable arg0, Object arg1) {
			RegionPanel.this.update();
		}
	}
	
	private static class MouseHandler extends MouseAdapter implements MouseListener{
		@Override
		public void mousePressed(MouseEvent e) {
			if(e.getSource() instanceof RegionPanel){
				RegionPanel source = (RegionPanel)e.getSource();
				source.selecting = !source.selecting;
				source.mouseInitX = e.getX();
				source.mouseInitY = e.getY();
				source.mouseX = e.getX();
				source.mouseY = e.getY();
				int mode = MODE_NORMAL;
				if((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0)
					mode = MODE_ADD;
				if((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0)
					mode = MODE_DELETE;
				source.doSelection(mode);
				source.repaint();
			}
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			Object source = e.getSource();
			if(source instanceof RegionPanel){
				((RegionPanel) source).mouseX = e.getX();
				((RegionPanel) source).mouseY = e.getY();
				int mode = MODE_NORMAL;
				if((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0)
					mode = MODE_ADD;
				if((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0)
					mode = MODE_DELETE;
				((RegionPanel) source).doSelection(mode);
				((RegionPanel) source).repaint();
				((RegionPanel) source).selecting = false;
			}
		}
		
		public void mouseDragged(MouseEvent e) {
			Object source = e.getSource();
			if(source instanceof RegionPanel){
				if(((RegionPanel) source).selecting){
					((RegionPanel) source).mouseX = e.getX();
					((RegionPanel) source).mouseY = e.getY();
					int mode = MODE_NORMAL;
					if((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0)
						mode = MODE_ADD;
					if((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0)
						mode = MODE_DELETE;
					((RegionPanel) source).doSelection(mode);
					((RegionPanel) source).repaint();
				}
			}
		}
	}
}
