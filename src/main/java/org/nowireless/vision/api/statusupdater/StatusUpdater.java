package org.nowireless.vision.api.statusupdater;

import org.nowireless.common.Initializable;
import org.nowireless.vision.api.VisionRuntime;


/**
 * Allows the Vision Engine status to be updated
 * @author nowireless

 */
public interface StatusUpdater extends Initializable {
	
	/**
	 * Updates the status with the given Engine
	 * <blockquote>
	 * For my sanity use {@code instanceof} for which VisionEngine is being used.
	 * </blockquote>
	 * @param engine
	 */
	public void update(VisionRuntime engine);
	
	public boolean isConnected();
}
