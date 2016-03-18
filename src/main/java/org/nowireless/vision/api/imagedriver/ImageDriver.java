package org.nowireless.vision.api.imagedriver;

import org.nowireless.common.engine.WatchedEngine;
import org.nowireless.vision.api.FrameUpdatable;

/**
 * The ImageUpdateEngine can be used to update frames in the {@link FrameUpdatable}
 * @author nowireless
 *
 */
public interface ImageDriver extends WatchedEngine {

	/**
	 * Sets the frame updatable.
	 * @param updatable The updatable
	 */
	public void setUpdatable(FrameUpdatable updatable);
	
	public FrameUpdatable getUpdateable();
	
}
