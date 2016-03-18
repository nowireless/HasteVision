package org.nowireless.vision.runtime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.nowireless.common.engine.impl.WatchedEngineAbstact;
import org.nowireless.configstore.ConfigStore;
import org.nowireless.mstore.MStore;
import org.nowireless.vision.api.Vision;
import org.nowireless.vision.api.VisionRuntime;
import org.nowireless.vision.api.fps.FPS;
import org.nowireless.vision.api.imagedriver.ImageDriver;
import org.nowireless.vision.api.process.ImageProcess;
import org.nowireless.vision.api.statusupdater.StatusUpdater;
import org.nowireless.vision.runtime.config.VisionRuntimeConfig;
import org.nowireless.vision.runtime.config.VisionRuntimeConfigStore;
import org.nowireless.vision.runtime.nt.NT;
import org.nowireless.vision.runtime.scraper.imagedriver.ImageDriverScraper;
import org.nowireless.vision.runtime.scraper.process.ProcessScraper;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;
import com.ni.vision.NIVision.RGBValue;

public abstract class VisionRuntimeAbstract extends WatchedEngineAbstact implements VisionRuntime {
	
	private static final Image NO_PROCESS = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
	
	private volatile boolean isPaused = false;
	private volatile boolean newSourceFrame = false;
	private volatile boolean cueFrameProcess = false;
	private volatile boolean saveLastSourceImage = false;
	
	
	private FPS fps = null;
	
	private StatusUpdater statusUpdater;
		
	private Gson gson;

	private NT nt;
//	private NTImageServer ntImageServer;
	
	private final Image sourceFrame = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
	private final Image debugFrame = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);;
	private final Image processedFrame = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);;
	private final Image lastProcessedSourceFrame = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);;
	
	private final Image currentFrame = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);;
	
	private final Object sourceFrameLock = new Object();
	private final Object debugFrameLock = new Object();
	private final Object processedFrameLock = new Object();
	private final Object lastProcessedSourceFrameLock = new Object();
	
	private VisionRuntimeConfig config;
	
	private final List<ImageProcess> availableProcesses = new ArrayList<ImageProcess>();
	private List<Class<? extends ImageProcess>> availableProcessesClass = Collections.emptyList();
	private ImageProcess process;
	
	private final List<ImageDriver> availableDrivers = new ArrayList<ImageDriver>();
	private List<Class<? extends ImageDriver>> avilableImageDriverClasses = Collections.emptyList();
	private ImageDriver imageDriver;
	

	
	public VisionRuntimeAbstract(long timeout) {
		super(timeout, "Vision");
	}
	
	public VisionRuntimeAbstract(long timeout, String threadName) {
		super(timeout, threadName);
	}


	protected void updateSource(Image image, boolean newFrame) {
		synchronized (sourceFrameLock) {
			this.newSourceFrame = newFrame;
			NIVision.imaqDuplicate(sourceFrame, image);
		}

	}
	
	@Override
	public void updateSource(Image image) {
		this.updateSource(image, true);
	}

	@Override
	public void updateDebug(Image image) {
		synchronized (debugFrameLock) {
			NIVision.imaqDuplicate(debugFrame, image);
		}
	}

	@Override
	public void updateProcessed(Image image) {
		synchronized (processedFrameLock) {
			NIVision.imaqDuplicate(processedFrame, image);
		}
	}
	
	protected void updateLastProcessedSourceFrame(Image image) {
		synchronized (lastProcessedSourceFrameLock) {
			NIVision.imaqDuplicate(lastProcessedSourceFrame, image);
		}
	}


	@Override
	public void getSourceFrame(Image frame) {
		synchronized (sourceFrameLock) {
			NIVision.imaqDuplicate(frame, sourceFrame);
		}
	}

	@Override
	public void getDebugFrame(Image frame) {
		synchronized (debugFrameLock) {
			NIVision.imaqDuplicate(frame, debugFrame);
		}
	}
	
	@Override
	public void getProcessedFrame(Image frame) {
		synchronized (processedFrameLock) {
			NIVision.imaqDuplicate(frame, processedFrame);
		}
	}
	
	protected void getLastProcessedSourceFrame(Image frame) {
		synchronized (lastProcessedSourceFrameLock) {
			NIVision.imaqDuplicate(frame, lastProcessedSourceFrame);
		}
	}

	@Override
	public void setProcess(ImageProcess process) {
		this.process = process;
	}

	@Override
	public ImageProcess getProcess() {
		return this.process;
	}

	@Override
	public void init() {
		log().info("Initializing");
		Validate.notNull(fps);
		Vision.setRuntime(this);
		
		this.getWatcher().disable();
		
		this.gson = Vision.getGsonBuilder().create();
		
		log().info("Starting MSTORE");
		MStore.get().init();
		MStoreShutdownHook.get().register();
		
		this.reset();
		
		VisionRuntimeConfigStore.initStore(this);
		this.config = VisionRuntimeConfigStore.get().get(ConfigStore.INSTANCE);
		if(this.config == null) {
			log().info("No config found, creating default");
			config = VisionRuntimeConfigStore.get().create(ConfigStore.INSTANCE);
		}
				
		if(this.config.useNT) {
			log().info("Enabling network table support");
			nt = new NT();
			nt.init();
		} else {
			nt = null;
		}
		
		if(process == null) {
			log().info("Looking for processes");
			ProcessScraper pScraper = new ProcessScraper(ImmutableList.of("org.nowireless"));
			List<Class<? extends ImageProcess>> processes = pScraper.getAvailableProcesses();
			this.availableProcessesClass = ImmutableList.copyOf(processes);
			for(Class<? extends ImageProcess> clazz : processes) {
				try {
					ImageProcess pInstance = clazz.newInstance();
					availableProcesses.add(pInstance);
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			log().info("Loaded processes {}", availableProcesses.size());
			
			if(!availableProcesses.isEmpty()) {
				//process = availableProcesses.get(1);
				config.lockConfig();
				String processName = config.getProcessClassName();
				config.unlockConfig();
				if(processName != null) {
					for(ImageProcess pInstance : availableProcesses) {
						if(pInstance.getClass().getName().equals(processName)) {
							this.process = pInstance;
						}
					}
	 			}
			}
		} else {
			log().info("Using preconfigured image process");
		}
		
		if(process != null) {
			log().info("Using process {}", process.getClass().getName());
			process.init();
		} else {
			log().warn("No process provied");
		}
		
		if(imageDriver == null) {
			log().info("Looking for Image Drivers");
			ImageDriverScraper dScraper = new ImageDriverScraper(ImmutableList.of("org.nowireless"));
			List<Class<? extends ImageDriver>> drivers = dScraper.getAvaiableDrivers();
			this.avilableImageDriverClasses = ImmutableList.copyOf(drivers);
			for(Class<? extends ImageDriver> clazz : drivers) {
				ImageDriver dInstance;
				try {
					dInstance = clazz.newInstance();
					availableDrivers.add(dInstance);
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			log().info("Loaded drivers {}", availableDrivers.size());
			
			if(!availableDrivers.isEmpty()) {
				//process = availableProcesses.get(1);
				config.lockConfig();
				String imageDriverName = config.getImageDriverClassName();
				config.unlockConfig();
				if(imageDriverName != null) {
					for(ImageDriver dInstance : availableDrivers) {
						if(dInstance.getClass().getName().equals(imageDriverName)) {
							this.imageDriver = dInstance;
						}
					}
				}
			}
		} else {
			log().info("Using preconfigured image driver");
		}
		if(imageDriver != null) {
			log().info("Image Driver {}", imageDriver.getClass().getName());
			imageDriver.setUpdatable(this);
			imageDriver.startEngine();
		} else {
			log().warn("No image update engine given");
			Image img = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
			this.updateSource(img);
			this.updateDebug(img);
			this.updateProcessed(img);
			img.free();
		}
		if(statusUpdater != null) {
			log().info("Using status updater {}", statusUpdater.getClass().getName());
			statusUpdater.init();
		} else {
			log().warn("No status updater given");
		}
		
		this.getWatcher().enable();
	}

	@Override
	public void deinit() {
		log().info("Deiniting");
		if(statusUpdater != null) {
			statusUpdater.deinit();
		}
		try {
			if(imageDriver != null) {
				imageDriver.stopEngineAndJoin();
			}
		} catch (InterruptedException e) {
			log().fatal("Could not join with Image Update Engine");;
			e.printStackTrace();
		}
		
		if(process != null) {
			process.deinit();
		}
		
		if(nt != null) {
			nt.deinit();
		}
		
	}

	@Override
	protected long getSleepTime() {
		return 10;
	}

	@Override
	public ImageDriver getUpdateEngine() {
		return imageDriver;
	}
	
	@Override
	public final void runTask() {
		//log().trace("Running Task");
		if(this.isPaused() && !cueFrameProcess) {
			fps.reset();
			this.onPause();
			log().trace("Paused");
			
			if(saveLastSourceImage) {
				saveLastSourceImage = false;
				String fileName = "./images/image-"+ new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())+".jpg";
				log().info("Save image {}", fileName);
				NIVision.imaqWriteFile(lastProcessedSourceFrame, fileName, new RGBValue());

			}
			
		} else {
			if(newSourceFrame || cueFrameProcess) {
				if(newSourceFrame) {
					log().trace("New Frame Available");
					newSourceFrame = false;
					this.getSourceFrame(currentFrame);
				}
				if(cueFrameProcess) {
					log().info("Reprocessing frame");
					cueFrameProcess = false;
					this.getLastProcessedSourceFrame(currentFrame);
				}
			
				
				Image srcFrame;
				srcFrame = currentFrame;
				
				if(process != null) {
					process.process(srcFrame);
					process.update(this);
				} else {
					this.updateDebug(NO_PROCESS);
					this.updateProcessed(NO_PROCESS);
				}
				
				this.updateLastProcessedSourceFrame(currentFrame);
				
				this.onPostProccess(srcFrame);
				
				fps.update();
			} else {
				//log().trace("No new frame");
			}
		}
		
		
		if(statusUpdater != null) {
			statusUpdater.update(this);
		}
		this.onLoop();
	}

	@Override public boolean isPaused() { return this.isPaused; }
	@Override public double getFPS() { return fps.getFPS(); }
	@Override public Gson getGson() { return gson; }
	@Override public void pause() { this.isPaused = true; }
	@Override public void unPause() { this.isPaused = false; }
	@Override public void setFPS(FPS fps) { this.fps = fps; }
	@Override public void setUpdater(StatusUpdater updater) { this.statusUpdater = updater; }
	@Override public void setUpdateEngine(ImageDriver update) { this.imageDriver = update; }
	@Override public StatusUpdater getUpdater() { return statusUpdater; }

	@Override
	public List<Class<? extends ImageProcess>> getAvailableProcesses() {
		return availableProcessesClass;
	}
	
	@Override
	public List<Class<? extends ImageDriver>> getAvailableImageDrivers() {
		return avilableImageDriverClasses;
	}
	
	@Override
	public void cueImageProcess() {
		this.cueFrameProcess = true;
	}
	
	@Override
	public void cueSaveImageSource() {
		this.saveLastSourceImage = true;
	}
	
	public VisionRuntimeConfig getConfig() {
		return this.config;
	}
	
	/**
	 * Resets the internal state.
	 */
	protected void reset() {
		this.isPaused = false;
		fps.reset();
	}
	
	/**
	 * Called when the {@link VisionRuntimeAbstract} is paused.
	 */
	protected abstract void onPause();
	
	/**
	 * Called right after the {@link VisionRuntimeAbstract} processes a new frame.
	 */
	protected abstract void onPostProccess(Image source);

	/**
	 * Called at the end of {@link #runTask()}.
	 */
	protected abstract void onLoop();

}
