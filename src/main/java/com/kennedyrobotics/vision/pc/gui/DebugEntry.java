package com.kennedyrobotics.vision.pc.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SpringLayout;
import javax.swing.JTextField;

public class DebugEntry extends JPanel {
	private JTextField textField;

	/**
	 * Create the panel.
	 */
	public DebugEntry(String name) {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JLabel lblname = new JLabel(name);
		springLayout.putConstraint(SpringLayout.NORTH, lblname, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblname, 10, SpringLayout.WEST, this);
		add(lblname);
		
		textField = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, textField, 0, SpringLayout.NORTH, lblname);
		springLayout.putConstraint(SpringLayout.WEST, textField, 6, SpringLayout.EAST, lblname);
		springLayout.putConstraint(SpringLayout.EAST, textField, -10, SpringLayout.EAST, this);
		add(textField);
		textField.setColumns(10);

	}
	
	public JTextField getTextField() {
		return textField;
	}
}
