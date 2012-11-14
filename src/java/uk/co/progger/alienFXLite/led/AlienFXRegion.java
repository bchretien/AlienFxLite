package uk.co.progger.alienFXLite.led;

import java.util.LinkedList;

public class AlienFXRegion {

	public final String name;
	public final String description;
	public final int regionId;
	public final int maxCommands;
	public final boolean canBlink;
	public final boolean canMorph;
	public final boolean canLight;
	
	
	
	public final LinkedList<AlienFXPowerMode> supportedModes;

	public AlienFXRegion(String name, String description, int regionId, int maxCommands, boolean canBlink, boolean canMorph, boolean canLight,
			LinkedList<AlienFXPowerMode> supportedModes) {
		super();
		AlienFXController.regionLookUp.put(name, this);
		this.description = description;
		this.regionId = regionId;
		this.name = name;
		this.canLight = canLight;
		this.canBlink = canBlink;
		this.canMorph = canMorph;
		this.maxCommands = maxCommands;
		this.supportedModes = new LinkedList<AlienFXPowerMode>(supportedModes);
	}
	
	
}