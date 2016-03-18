package org.nowireless.vision.api;

import java.util.List;

import org.nowireless.common.engine.Engine;
import org.nowireless.mstore.MStoreUser;
import org.nowireless.vision.api.FrameUpdatable;
import org.nowireless.vision.api.Pausable;
import org.nowireless.vision.api.fps.FPS;
import org.nowireless.vision.api.fps.FPSSource;
import org.nowireless.vision.api.imagedriver.ImageDriver;
import org.nowireless.vision.api.process.ImageProcess;
import org.nowireless.vision.api.statusupdater.StatusUpdater;

import com.ni.vision.NIVision.Image;

/**
 * The VisionEngine is used as the main Engine being used Run/Controll all aspects of the Vision Program.
 * @author nowireless
 *
 * @param <T> The process being used
 * @param <V> The samples being used
 */
public interface VisionRuntime extends Engine, FrameUpdatable, Pausable, FPSSource, MStoreUser {
	
	/**
	 * Sets the {@link ImageProcess} being used.
	 * @param process The process
	 */
	public void setProcess(ImageProcess process);
	
	/**
	 * Gets the process.
	 * @return The process
	 */
	public ImageProcess getProcess();
	
	public List<Class<? extends ImageProcess>> getAvailableProcesses();
	public List<Class<? extends ImageDriver>> getAvailableImageDrivers();
	
	/**
	 * Copies the the source frame to the given {@link Mat} image.
	 * @param dst The dst image
	 */
	public void getSourceFrame(Image dst);
	
	/**
	 * Copies the the debug frame to the given {@link Mat} image.
	 * @param dst The dst image
	 */
	public void getDebugFrame(Image dst);
	
	/**
	 * Copies the the debug frame to the given {@link Mat} image.
	 * @param dst The dst image
	 */
	public void getProcessedFrame(Image dst);
	
	/**
	 * Sets the Update Engine
	 * @param update The engine
	 */
	public void setUpdateEngine(ImageDriver update);
	
	/**
	 * Gets the {@link ImageDriver} currently in use.
	 * @return The ImageEngine
	 */
	public ImageDriver getUpdateEngine();
	
	/**
	 * Sets the FPS calculator
	 * @param fps The fps Calculator
	 */
	public void setFPS(FPS fps);
	
	/**
	 * Sets the updater to use.
	 * @param updater The updater to be used
	 */
	public void setUpdater(StatusUpdater updater);
	
	/**
	 * Gets the updater being used
	 * @return The updater
	 */
	public StatusUpdater getUpdater();
	
	/**
	 * When the runtime is paused it can reprocess the current frame.
	 */
	public void cueImageProcess();
	
	/**
	 * Tell the runtime to save the last processed source image
	 */
	public void cueSaveImageSource();
}
