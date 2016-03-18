package org.nowireless.vision.api.loader;

import org.nowireless.vision.api.VisionRuntime;

/**
 * Helps load and set up the {@link VisionRuntime}.
 * @author nowireless
 *
 */
public interface VisionLoader {
	
	/**
	 * Sets up a new Instance of a Vision Engine
	 * @return The Engine
	 */
	public VisionRuntime load();
}
