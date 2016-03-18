package com.kennedyrobotics.vision.pc;

import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;

import org.apache.commons.lang3.Validate;
import org.nowireless.vision.api.gui.ConfigPopup;

public class Vision2016ConfigPopup implements ConfigPopup, Runnable {

	
	private Vision2016ConfigDialog dialog;
	private final ConfigWrapper config;
	
	public Vision2016ConfigPopup(ConfigWrapper config) {
		this.config = Validate.notNull(config);
		SwingUtilities.invokeLater(this);
	}
	
	@Override public String getName() { return "Vision 2016 Config Config"; }

	@Override
	public void actionPerformed(ActionEvent e) {
		this.dialog.setVisible(true);
	}
	
	@Override
	public void run() {
		dialog = new Vision2016ConfigDialog(config);
	}
	
}
