package com.kennedyrobotics.vision.pc.rt;

import org.nowireless.vision.api.process.ImageProcess;
import org.nowireless.vision.runtime.standalone.StandaloneVisionLoader;

import com.kennedyrobotics.vision.pc.VisionProcessWrapper;

public abstract class Vision2016StandaloneLoader extends StandaloneVisionLoader {

	@Override
	protected ImageProcess getProcess() {
		return new VisionProcessWrapper();
	}
	
}
