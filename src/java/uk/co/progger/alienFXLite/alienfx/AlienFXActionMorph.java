package uk.co.progger.alienFXLite.alienfx;

import java.awt.Color;

import uk.co.progger.alienFXLite.gui.AlienFXTexts;
import uk.co.progger.alienFXLite.gui.ColorModel;

/**
 * Class for a morph between two colors
 */
public class AlienFXActionMorph extends AlienFXActionBlink{	
	private static final long serialVersionUID = 1L;

	private ColorModel model = new ColorModel();
	
	public AlienFXActionMorph(Color c, Color c2) {
		super(c);
		model.setColor(c2);
		model.addObserver(new ModelObserver(model));
	}
	
	public Color getMorphToColor() {
		return model.getColor();
	}
	
	public ColorModel getMorphToColorModel(){
		return model;
	}
	
	@Override
	public Color getTrailingColor() {
		return getMorphToColor();
	}
	
	@Override
	public ColorModel getTrailingColorModel() {
		return model;
	}
	
	public String getName() {
		return AlienFXTexts.ACTION_MORPH_TEXT;
	}
	
	public AlienFXAction clone() {
		return new AlienFXActionMorph(getColor(), getMorphToColor());
	}
	
	public void loaded() {
		super.loaded();
		model.addObserver(new ModelObserver(model));
	}
	
	public void setMorphToColor(Color morphToColor) {
		model.setColor(morphToColor);
		setChanged();
		notifyObservers(this);
	}
}
