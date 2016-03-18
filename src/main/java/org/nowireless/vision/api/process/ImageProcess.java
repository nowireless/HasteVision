package org.nowireless.vision.api.process;

import org.nowireless.common.Initializable;
import org.nowireless.vision.api.FrameUpdatable;

import com.ni.vision.NIVision.Image;

/**
 * A Generic process that uses a {@link Mat} frame.
 * @author nowireless
 *
 */
public interface ImageProcess extends Initializable {
	
	/**
	 * Processes the provided {@link Mat} and return the samples used for scoring.
	 * 
	 * @param image The image to process
	 * @return The samples used for scoring
	 */
	public void process(Image image);
	
	public void update(FrameUpdatable updatable);
}
