package uk.co.progger.alienFXLite.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

class DashedBorder implements Border {
	private final Insets insets = new Insets(1, 1, 1, 1);
	private final int length = 5;
	private final int space = 3;
	public boolean isBorderOpaque() {
		return false;
	}
	public void paintBorder(Component c, Graphics g, int x, int y,
			int width, int height) {
		g.setColor(Color.RED);
		// --- draw horizontal ---
		for (int i = 0; i < width; i += length) {
			g.drawLine(i, y, i + length, y);
			g.drawLine(i, height - 1, i + length, height - 1);
			i += space;
		}
		// --- draw vertical ---
		for (int i = 0; i < height; i += length) {
			g.drawLine(0, i, 0, i + length);
			g.drawLine(width - 1, i, width - 1, i + length);
			i += space;
		}
	}
	public Insets getBorderInsets(Component c) {
		return insets;
	}
}
