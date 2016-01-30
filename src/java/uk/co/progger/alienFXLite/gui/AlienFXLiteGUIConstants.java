package uk.co.progger.alienFXLite.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Stroke;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class AlienFXLiteGUIConstants {
	public static int DEFAULT_PAD = 5;
	public static final int BUTTON_INSET = 1;
	public static final int ACTION_LENGTH = 100;
	
	
	public static final ImageIcon removeButtonImageIcon = new ImageIcon(AlienFXResources.ERASE_ACTION_ICON_IMAGE);
	public static final Insets buttonInsets = new Insets(1, 1, 1, 1);
	public static final Dimension actionPanelFXDimension = new Dimension(AlienFXLiteGUIConstants.ACTION_LENGTH, 24);
	public static final Dimension maxDimension = new Dimension(Short.MAX_VALUE,Short.MAX_VALUE);
	
	public static final Stroke borderStroke = new BasicStroke(2);
	public static final Color regularBorderColor = new JPanel().getBackground().darker();
	public static final Border regularBorder = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(regularBorderColor), BorderFactory.createEmptyBorder(1, 0, 1, 0)); 
	public static final Border selectedBorder = BorderFactory.createCompoundBorder(new DashedBorder(), BorderFactory.createEmptyBorder(1, 0, 1, 0));
	
}
