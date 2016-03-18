package org.nowireless.vision.api;

import com.ni.vision.NIVision.Image;

public interface FrameUpdatable {
	/**
	 * Updates the {@link Mat} Source Frame.
	 * @param image
	 */
	public void updateSource(Image image);
	
	/**
	 * Updates the {@link Mat} Debug frame.
	 * @param image
	 */
	public void updateDebug(Image image);
	
	/**
	 * Updates the {@link Mat} Processed Frame.
	 * @param image
	 */
	public void updateProcessed(Image  image);

}
