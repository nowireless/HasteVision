package com.kennedyrobotics.vision.pc.rt;

import org.nowireless.nivision.NIFileImageUpdater;
import org.nowireless.vision.api.imagedriver.ImageDriver;

public class FileVision2016StandaloneLoader extends Vision2016StandaloneLoader {

	@Override
	protected ImageDriver getImageUpdateEngine() {
		return new NIFileImageUpdater();
	}
	
	public static void main(String[] args) {
		new FileVision2016StandaloneLoader().load().startEngine();
	}
	
}
