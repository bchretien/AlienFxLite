package uk.co.progger.alienFXLite.led;

public class AlienFXPowerMode {
	public final String name;
	public final String description;
	public final int blockId;
	
	public AlienFXPowerMode(String name, String description, int blockId) {
		super();
		AlienFXController.powermodeLookUp.put(name, this);
		this.description = description;
		this.blockId = blockId;
		this.name = name;
	}
}
