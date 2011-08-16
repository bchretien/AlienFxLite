package uk.co.progger.alienFXLite.alienfx;

import java.awt.Color;
import java.io.Serializable;
import java.util.Observable;

import uk.co.progger.alienFXLite.gui.ColorModel;

/**
 * Abstract class which defines an alienFX action. This Action is one of the "things" which can be set to an LED region. (e.g. blink, or color)
 * 
 */
public abstract class AlienFXAction extends Observable implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/**
	 * @return human readable description for the action 
	 */
	public abstract String getName();
	
	/**
	 * Clone the action. Needed to clone profiles
	 */
	public abstract AlienFXAction clone();
	
	/**
	 * Get the leading color. e.g. the color which is at the beginning of the action visible
	 * @return
	 */
	public abstract Color getLeadingColor();
	
	/**
	 * Get the leading color. e.g. the color which is at the beginning of the action visible
	 * @return
	 */
	public abstract ColorModel getLeadingColorModel();
	
	/**
	 * Get the trailing color. e.g. the color which is at the end of the action visible
	 * @return
	 */
	public abstract Color getTrailingColor();
	
	/**
	 * Get the trailing color. e.g. the color which is at the end of the action visible
	 * @return
	 */
	public abstract ColorModel getTrailingColorModel();
	
	/**
	 * This action has been loaded from memory. Do all the initialization which is usually done upon creation
	 */
	public abstract void loaded();
}
