package org.nowireless.vision.api;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.GsonBuilder;

public class Vision {
	
	private static Logger LOG = LogManager.getLogger();
	private static VisionRuntime RUNTIME;
	public static VisionRuntime getRuntime() { return RUNTIME; }

	
	public static void setRuntime(VisionRuntime engine) {
		RUNTIME = Validate.notNull(engine);
	}
	
	
	public static GsonBuilder getGsonBuilder() {
		return new GsonBuilder().setPrettyPrinting();
	}

	
	public static void loadNatives() {
		loadCore();
	}
	
	public static void loadCore() {
		LOG.info("Loading core");
		//OpenCV.loadShared();
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
}
