package org.nowireless.vision.api;

/**
 * Convince methods for pausing and unpausing some sort of logic.
 * @author nowireless
 *
 */
public interface Pausable {

	/**
	 * Pauses the logic
	 */
	public void pause();
	
	/**
	 * UnPauses the logic
	 */
	public void unPause();
	
	/**
	 * Checks to see if if the logic is paused
	 * @return the state
	 */
	public boolean isPaused();
}
