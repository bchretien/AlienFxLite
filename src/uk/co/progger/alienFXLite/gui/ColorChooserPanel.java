package uk.co.progger.alienFXLite.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class ColorChooserPanel extends JPanel{	
	
	private static final long serialVersionUID = 1L;
	private static final int PREVIEW_PANEL_SIZE = 30;
	
	private ColorPanel colors;
	private ColorBasePanel colorBase;
	private JPanel preview;
	private JLabel previewLabel;
	private ColorModel model;
	
	private JLabel redLabel;
	private JLabel greenLabel;
	private JLabel blueLabel;
	
	private JFormattedTextField redField;
	private JFormattedTextField greenField;
	private JFormattedTextField blueField;
	
	public ColorChooserPanel(ColorModel model){
		this.model = model;
		
		model.addObserver(new ColorListener());
		model.addObserver(new ModelListener());
		ColorModel baseColorModel = new ColorModel();
		
		colorBase = new ColorBasePanel(baseColorModel);
		colors = new ColorPanel(model,baseColorModel);
		
		redLabel = new JLabel("R:");
		greenLabel = new JLabel("G:");
		blueLabel = new JLabel("B:");
		
		redField = new JFormattedTextField(255);
		greenField = new JFormattedTextField(255);
		blueField = new JFormattedTextField(255);
		
		redField.setPreferredSize(redField.getPreferredSize());
		greenField.setPreferredSize(redField.getPreferredSize());
		blueField.setPreferredSize(redField.getPreferredSize());
		
		redField.addActionListener(new ActionHandler());
		greenField.addActionListener(new ActionHandler());
		blueField.addActionListener(new ActionHandler());
		
		preview = new JPanel();
		previewLabel = new JLabel(AlienFXTexts.PREVIEW_LABEL_TEXT);
		preview.add(previewLabel);
		preview.setBackground(model.getColor());
		init();
	}
	
	private void init(){
		SpringLayout layout = new SpringLayout();
		this.setLayout(layout);
		layout.putConstraint(SpringLayout.SOUTH, this, AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.SOUTH, preview);
		
		layout.putConstraint(SpringLayout.NORTH, colors, AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, colors, AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.WEST, this);
				
		layout.putConstraint(SpringLayout.NORTH, colorBase, 0, SpringLayout.NORTH, colors);
		layout.putConstraint(SpringLayout.WEST, colorBase, AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.EAST, colors);
		layout.putConstraint(SpringLayout.EAST, this, AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.EAST, colorBase);
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, redLabel, 0, SpringLayout.VERTICAL_CENTER, redField);
		layout.putConstraint(SpringLayout.WEST, redLabel, 0, SpringLayout.WEST, colors);
		
		layout.putConstraint(SpringLayout.NORTH, redField, AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.SOUTH, colors);
		layout.putConstraint(SpringLayout.WEST, redField, AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.EAST, redLabel);
		
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, greenLabel, 0, SpringLayout.VERTICAL_CENTER, greenField);
		layout.putConstraint(SpringLayout.WEST, greenLabel, AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.EAST, redField);
		
		layout.putConstraint(SpringLayout.NORTH, greenField, AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.SOUTH, colors);
		layout.putConstraint(SpringLayout.WEST, greenField, AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.EAST, greenLabel);
		
		
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, blueLabel, 0, SpringLayout.VERTICAL_CENTER, blueField);
		layout.putConstraint(SpringLayout.WEST, blueLabel, AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.EAST, greenField);
		
		layout.putConstraint(SpringLayout.NORTH, blueField, AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.SOUTH, colors);
		layout.putConstraint(SpringLayout.WEST, blueField, AlienFXLiteGUIConstants.DEFAULT_PAD, SpringLayout.EAST, blueLabel);
		
		layout.putConstraint(SpringLayout.SOUTH, preview, PREVIEW_PANEL_SIZE, SpringLayout.NORTH, preview);
		layout.putConstraint(SpringLayout.NORTH, preview, AlienFXLiteGUIConstants.DEFAULT_PAD*2, SpringLayout.SOUTH, redField);
		layout.putConstraint(SpringLayout.EAST, preview, 0, SpringLayout.EAST, colorBase);
		layout.putConstraint(SpringLayout.WEST, preview, 0, SpringLayout.WEST, colors);
		
		add(colorBase);
		add(colors);
		
		add(redLabel);
		add(redField);
		add(greenLabel);
		add(greenField);
		add(blueLabel);
		add(blueField);
		
		add(preview);
		setMaximumSize(getPreferredSize());
		setMinimumSize(getPreferredSize());
	}
	
	private class ModelListener implements Observer{
		public void update(Observable arg0, Object arg1) {
			redField.setValue(model.getColor().getRed());
			greenField.setValue(model.getColor().getGreen());
			blueField.setValue(model.getColor().getBlue());
		}		
	}
	
	private class ActionHandler implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			model.setColor(new Color(((Integer)redField.getValue()) & 0xFF, ((Integer)greenField.getValue()) & 0xFF, ((Integer)blueField.getValue()) & 0xFF ), this);
		}		
	}
	
	private class ColorListener implements Observer{
		public void update(Observable o, Object arg) {
			preview.setBackground(model.getColor());
			previewLabel.setForeground(new Color(model.getColor().getRGB() ^ 0xFFFFFF));
		}
	}
}
