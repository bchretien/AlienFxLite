package uk.co.progger.alienFXLite.gui;

import javax.swing.JPanel;

public abstract class ActionPanel extends JPanel{
	private static final long serialVersionUID = 1L;

	protected ColorModel model;
	private int index;
	protected ActionClipboard clipboard;

	public ActionPanel(ColorModel model, ActionClipboard clipboard, int index) {
		super();
		this.model = model;
		this.index = index;
		this.clipboard = clipboard;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
