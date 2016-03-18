package org.nowireless.vision.api.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.nowireless.vision.api.Pausable;

public class PauseButtonListener implements ActionListener {

	private transient final Pausable pause;
	
	public PauseButtonListener(Pausable pause) {
		this.pause = pause;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(pause.isPaused()) {
			pause.unPause();
		} else {
			pause.pause();
		}
	}

}
