package org.nowireless.vision.api.fps;

/**
 * Used to calculate FPS
 * @author nowireless
 *
 */
public interface FPS extends FPSSource {
	
	/**
	 * Says a new frame has been processed
	 */
	public void update();
	
	/**
	 * Resets the counter
	 */
	public void reset()
;}
