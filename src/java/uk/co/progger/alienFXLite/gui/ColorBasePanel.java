package uk.co.progger.alienFXLite.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

public class ColorBasePanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private static final int PANEL_WIDTH = 10;
	private static final int TOTAL_STEPS = 255*7;
	
	//cache
	private Color[] colors;
	private int selection = 0;
	
	private ColorModel model;
	
	public ColorBasePanel(ColorModel model){
		MouseListenerImpl listener = new MouseListenerImpl();
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);
		setPreferredSize(new Dimension(PANEL_WIDTH, ColorPanel.PANEL_SIZE));
		setMaximumSize(getPreferredSize());
		setMinimumSize(getPreferredSize());
		this.model = model;
		this.model.addObserver(new BaseColorHandler());
		update();
	}
	
	private void update(){
		int length = getHeight();
		colors = new Color[length];
		
		int stepSize = (int)(((double)TOTAL_STEPS)/((double)length));
		int red = 255, green = 0, blue= 0;
		
		int i = 0;
		
		while(green < 255 && i < length){
			green+=stepSize;
			green = green > 255 ? 255 : green;
			colors[i++] = new Color(red, green, blue);
		}
		green = 255;
		
		while(red > 0 && i < length){
			red-=stepSize;
			red = red < 0 ? 0 : red;
			colors[i++] = new Color(red, green, blue);
		}
		red = 0;
		
		while(blue < 255 && i < length){
			blue+=stepSize;
			blue = blue > 255 ? 255 : blue;
			colors[i++] = new Color(red, green, blue);
		}
		blue = 255;
		
		while(green > 0 && i < length){
			green-=stepSize;
			green = green < 0 ? 0 : green;
			colors[i++] = new Color(red, green, blue);
		}
		green = 0;
		
		while(red < 255 && i < length){
			red+=stepSize;
			red = red > 255 ? 255 : red;
			colors[i++] = new Color(red, green, blue);
		}
		red = 255;
		
		while(blue > 0 && i < length){
			blue-=stepSize;
			blue = blue < 0 ? 0 : blue;
			colors[i++] = new Color(red, green, blue);
		}
		for(; i < length; i++)
			colors[i] = new Color(red,green,blue);
		
		if(colors.length > 0)
			model.setColor(colors[0], this);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int height = getHeight();
		int width = getWidth();
		if(colors.length != height)
			update();

		for(int i = 0; i < height; i++){
			g.setColor(colors[i]);
			g.drawLine(0, i, width, i);
		}
		
		g.setColor(Color.WHITE);
		g.drawLine(0, selection-2, width, selection-2);
		g.drawLine(0, selection-3, width, selection-3);
		
		g.setColor(Color.BLACK);
		g.drawLine(0, selection-4, width, selection-4);
		g.drawLine(0, selection-5, width, selection-5);
		
		g.setColor(Color.WHITE);
		g.drawLine(0, selection+2, width, selection+2);
		g.drawLine(0, selection+3, width, selection+3);
		
		g.setColor(Color.BLACK);
		g.drawLine(0, selection+4, width, selection+4);
		g.drawLine(0, selection+5, width, selection+5);
	}
	
	private void setPoint(Point p){
		int s = p.y;
		s = s < 0 ? 0 : s;
		s = s >= getHeight() ? getHeight()-1 : s;
		selection = s;
		model.setColor(colors[s], this);
		repaint();
	}
	
	private void gotoModelColor(){
		//if this is too slow, then we need to make it faster
		int cr = model.getColor().getRed();
		int cg = model.getColor().getGreen();
		int cb = model.getColor().getBlue();
		
		int best = 1000;
		int loc = 0;
		for(int i = 0; i < colors.length; i++){
			int r = colors[i].getRed();
			int g = colors[i].getGreen();
			int b = colors[i].getBlue();
			
			int distance = (cr-r)*(cr-r) + (cg-g)*(cg-g) + (cb-b)*(cb-b);
			if(distance < best){
				best = distance;
				loc = i;
			}
		}
		selection = loc;
		repaint();
	}
	
	private class BaseColorHandler implements Observer{
		public void update(Observable source, Object cause) {
			if(cause != ColorBasePanel.this){
				gotoModelColor();
			}
		}		
	}
	
	private class MouseListenerImpl implements MouseMotionListener, MouseListener{
		public void mouseDragged(MouseEvent e) {setPoint(e.getPoint());}
		public void mouseMoved(MouseEvent e) {}
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {setPoint(e.getPoint());}
		public void mouseReleased(MouseEvent e) {}	
	}
}
