package uk.co.progger.alienFXLite.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class DeviceInfoPanel extends JPanel
{	
	
	private static final long serialVersionUID = 1L;

	private JLabel deviceLabel;
	private String deviceTitle = "<b>Device:</b>";

	public DeviceInfoPanel(String deviceName)
	{
		this.setLayout(new BorderLayout(
				AlienFXLiteGUIConstants.DEFAULT_PAD,
				AlienFXLiteGUIConstants.DEFAULT_PAD));

		String text = "<html>" + deviceTitle + " " + deviceName + "</html>";
		deviceLabel = new JLabel(text, JLabel.CENTER);
		Border emptyBorder = BorderFactory.createEmptyBorder(
				AlienFXLiteGUIConstants.DEFAULT_PAD,
				2*AlienFXLiteGUIConstants.DEFAULT_PAD,
				AlienFXLiteGUIConstants.DEFAULT_PAD,
				AlienFXLiteGUIConstants.DEFAULT_PAD);

		deviceLabel.setBorder(emptyBorder);
		this.add(deviceLabel, BorderLayout.LINE_START);
	}
}
