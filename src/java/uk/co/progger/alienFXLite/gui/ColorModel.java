package uk.co.progger.alienFXLite.gui;

import java.awt.Color;
import java.io.Serializable;
import java.util.Observable;

public class ColorModel extends Observable implements Serializable{
	private static final long serialVersionUID = 1L;
	private Color color = Color.red;

	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		setColor(color,null);
	}	
	
	public void setColor(Color color, Object source) {
		this.color = color;
		setChanged();
		notifyObservers(source);
	}	
}
