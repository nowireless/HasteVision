package org.nowireless.vision.loader;

import org.nowireless.vision.api.VisionRuntime;
import org.nowireless.vision.api.fps.FPS;
import org.nowireless.vision.api.imagedriver.ImageDriver;
import org.nowireless.vision.api.loader.VisionLoader;
import org.nowireless.vision.api.process.ImageProcess;
import org.nowireless.vision.api.statusupdater.StatusUpdater;
import org.nowireless.vision.runtime.fps.MovingAverageFPS;

/**
 * Helps load the VisionEngine
 * @author nowireless
 *
 */
public abstract class VisionLoaderAbstract implements VisionLoader {
	
	@Override
	public VisionRuntime load() {
		
		VisionRuntime ret = getEngine();
		ret.setProcess(getProcess());
		ret.setUpdateEngine(getImageUpdateEngine());
		ret.setUpdater(getStatusUpdater());
		ret.setFPS(getFps());
		return ret;
	}

	/**
	 * Get the Vision Engine Being used
	 * @return The Engine
	 */
	protected abstract VisionRuntime getEngine();
	
	/**
	 * Gets the process being used
	 * @return The process
	 */
	protected ImageProcess getProcess() {
		return null;
	}

	
	/**
	 * Gets the ImageUpdateEngine
	 * @return The ImageUpdateEngine
	 */
	protected ImageDriver getImageUpdateEngine() {
		return null;
	}
	
	/**
	 * Gets the FPS updater
	 * @return The FPS
	 */
	protected FPS getFps() {
		return new MovingAverageFPS();
	}
	
	/**
	 * Gets the Status updater
	 * @return The status updater
	 */
	protected StatusUpdater getStatusUpdater() {
		return null;
	}
	
}
