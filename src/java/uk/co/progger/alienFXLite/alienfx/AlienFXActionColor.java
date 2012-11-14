package uk.co.progger.alienFXLite.alienfx;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import uk.co.progger.alienFXLite.gui.AlienFXTexts;
import uk.co.progger.alienFXLite.gui.ColorModel;

/**
 * Class for a simple constant color
 */
public class AlienFXActionColor extends AlienFXAction{
	private static final long serialVersionUID = 1L;
	private ColorModel model = new ColorModel();
	
	public AlienFXActionColor(Color c){
		model.setColor(c);
		model.addObserver(new ModelObserver(model));
	}

	public Color getColor() {
		return model.getColor();
	}
	
	public ColorModel getColorModel(){
		return model;
	}
	
	@Override
	public Color getTrailingColor() {
		return getColor();
	}
	
	@Override
	public Color getLeadingColor() {
		return getColor();
	}
	
	public void setColor(Color c){
		model.setColor(c);
		setChanged();
		notifyObservers(this);
	}

	public String getName() {
		return AlienFXTexts.ACTION_MORPH_TEXT;
	}

	public AlienFXAction clone() {
		return new AlienFXActionColor(model.getColor());
	}

	public void loaded() {
		model.addObserver(new ModelObserver(model));
	}
	
	public ColorModel getLeadingColorModel() {
		return getColorModel();
	}

	public ColorModel getTrailingColorModel() {
		return getColorModel();
	}
	
	protected class ModelObserver implements Observer{
		private ColorModel model;
		public ModelObserver(ColorModel model) {
			super();
			this.model = model;
		}
		public void update(Observable arg0, Object arg1) {
			AlienFXActionColor.this.setChanged();
			AlienFXActionColor.this.notifyObservers(AlienFXActionColor.this);
		}
		protected void finalize() throws Throwable {
			super.finalize();
			if(model != null)
				model.deleteObserver(this);
		}
	}
}
