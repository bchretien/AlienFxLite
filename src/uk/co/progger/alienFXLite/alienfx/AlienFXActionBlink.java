package uk.co.progger.alienFXLite.alienfx;

import java.awt.Color;

import uk.co.progger.alienFXLite.gui.AlienFXTexts;

/**
 * Class for a simple constant color which blinks
 */
public class AlienFXActionBlink extends AlienFXActionColor{
	private static final long serialVersionUID = 1L;

	public AlienFXActionBlink(Color c) {
		super(c);
	}
	
	@Override
	public AlienFXAction clone() {
		return new AlienFXActionBlink(getColor());
	}
	
	@Override
	public String getName() {
		return AlienFXTexts.ACTION_MORPH_TEXT;
	}
}
