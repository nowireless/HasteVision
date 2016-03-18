package com.kennedyrobotics.vision.pc.rt;

import org.nowireless.nivision.NICameraUpdater;
import org.nowireless.vision.api.imagedriver.ImageDriver;

public class CameraVision2016StandaloneLoader extends Vision2016StandaloneLoader {

	@Override
	protected ImageDriver getImageUpdateEngine() {
		return new NICameraUpdater();
	}
	
	public static void main(String[] args) {
		new CameraVision2016StandaloneLoader().load().startEngine();
	}
}
