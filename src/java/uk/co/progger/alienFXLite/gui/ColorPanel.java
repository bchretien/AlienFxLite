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

public class ColorPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	protected static final int PANEL_SIZE = 200;
	private static final int CIRCLE_SIZE = 8;
	private static final int SMALL_CIRCLE_SIZE = 6;
	
	
	//this color has to be in the form of (0xFF x x) OR (x 0xFF x) OR (x x 0xFF) where x is a value between 0 and 0xFF
	private ColorModel baseColorModel;
	
	private ColorModel colorModel;
	private Point curPos = new Point(0,0);
	
	//caching
	private Color[][] colorMatrix;
	private Color cachedColor;
	
	public ColorPanel(ColorModel chosenColor, ColorModel baseColorModel){
		this.baseColorModel= baseColorModel;
		baseColorModel.addObserver(new BaseColorObserver());
		
		MouseListenerImpl listener = new MouseListenerImpl();
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);
		this.setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
		this.setMaximumSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
		this.setMinimumSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
		this.colorModel = chosenColor;
		this.colorModel.addObserver(new ColorObserver());
	}
	
	
	protected void update(boolean setColor){
		int width = this.getWidth();
		int height = this.getHeight();
		
		Color baseColor = baseColorModel.getColor();
		
		if(colorMatrix != null && cachedColor == baseColor)
			return;
		
		colorMatrix = new Color[getWidth()][getHeight()];
		cachedColor = baseColor;
		
		int red,green,blue;
		int max;
		
		double stepYRed = ((double)(baseColor.getRed())/((double)height-1));
		double stepYGreen = ((double)(baseColor.getGreen())/((double)height-1));
		double stepYBlue = ((double)(baseColor.getBlue())/((double)height-1));
		double stepYMax = (255.0/((double)height));
		
		for(int y = 0; y < height; y++){
			red = baseColor.getRed() - (int)(stepYRed*y);
			green = baseColor.getGreen() - (int)(stepYGreen*y);
			blue = baseColor.getBlue() - (int)(stepYBlue*y);
			
			max = (int)(255.0-stepYMax*y);
			
			double stepXRed = ((double)(max-red))/((double)width-1);
			double stepXGreen = ((double)(max-green))/((double)width-1);
			double stepXBlue = ((double)(max-blue))/((double)width-1);
			
			int redx,greenx,bluex;
			for(int x=0; x < width; x++){
				redx = red + (int)(stepXRed*x);
				greenx = green + (int)(stepXGreen*x);
				bluex = blue + (int)(stepXBlue*x);
				colorMatrix[x][y] = new Color(redx & 0xFF, greenx & 0xFF, bluex & 0xFF);
			}
		}
		if(setColor)
			colorModel.setColor(colorMatrix[curPos.x][curPos.y], this);
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int width = this.getWidth();
		int height = this.getHeight();
		if(colorMatrix == null)
			return;
		for(int y = 0; y < height; y++){
			for(int x=0; x < width; x++){
				g.setColor(colorMatrix[x][y]);
				g.fillRect(x, y, 1, 1);
			}
		}
		g.setColor(Color.black);
		g.drawOval(curPos.x-CIRCLE_SIZE/2, curPos.y-CIRCLE_SIZE/2, CIRCLE_SIZE, CIRCLE_SIZE);
		g.setColor(Color.white);
		g.drawOval(curPos.x-SMALL_CIRCLE_SIZE/2, curPos.y - SMALL_CIRCLE_SIZE/2, SMALL_CIRCLE_SIZE, SMALL_CIRCLE_SIZE);
	}
	
	protected void setPoint(Point p){
		int x = p.x;
		x = x < 0 ? 0 : x;
		x = x >= getWidth() ? getWidth()-1 : x;
		
		int y = p.y;
		y = y < 0 ? 0 : y;
		y = y >= getHeight() ? getHeight()-1 : y;
		curPos = new Point(x,y);
		colorModel.setColor(colorMatrix[x][y], this);
		repaint();
	}
	
	private void goToColor(){
		//figure out the base color:
		int r = colorModel.getColor().getRed();
		int g = colorModel.getColor().getGreen();
		int b = colorModel.getColor().getBlue();
		
		int smallest = Math.min(r,Math.min(g, b));
		int biggest = Math.max(r,Math.max(g, b));
		
		double w = (double)getWidth();
		double h = (double)getWidth();
		
		double x = (w * smallest) / (double)biggest;
		Double baseRed = ((w*r - biggest*x)/(w-x));
		Double baseGreen = ((w*g - biggest*x)/(w-x));
		Double baseBlue = ((w*b - biggest*x)/(w-x));
		
		if(!(baseRed > 255 || baseRed.isNaN() || baseGreen > 255|| baseGreen.isNaN() || baseBlue > 255|| baseBlue.isNaN())) {
			double y = h-((biggest*h)/255);
	
			baseRed = Math.ceil(baseRed*h/(h-y));
			baseGreen = Math.ceil(baseGreen*h/(h-y));
			baseBlue = Math.ceil(baseBlue*h/(h-y));	
			baseColorModel.setColor(new Color(baseRed.intValue() > 255 ? 255 : baseRed.intValue()  , 
											  baseGreen.intValue() > 255 ? 255 : baseGreen.intValue() , 
											  baseBlue.intValue() > 255 ? 255 : baseBlue.intValue() ), this);
		}
		
		//now calculate x and y
		int y1 = (int)((255-biggest) * h) / 255;
		int x1;
		if(biggest > 0)
			x1 = (int)(smallest*w)/biggest;
		else
			x1 = (int)w;	
		curPos = new Point(x1, y1);
		repaint();
	}
	
	private class ColorObserver implements Observer{
		public void update(Observable o, Object arg) {
			if(arg != ColorPanel.this)
				ColorPanel.this.goToColor();
		}
	}
	
	private class BaseColorObserver implements Observer{
		public void update(Observable o, Object arg) {
			ColorPanel.this.update(arg != ColorPanel.this);
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
