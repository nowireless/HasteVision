package com.kennedyrobotics.vision.pc;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nowireless.configstore.ConfigStore;
import org.nowireless.vision.api.FrameUpdatable;
import org.nowireless.vision.api.Vision;
import org.nowireless.vision.api.gui.ConfigPopup;
import org.nowireless.vision.api.gui.GUIProvider;
import org.nowireless.vision.api.process.ImageProcess;
import org.nowireless.vision.api.process.annotations.Process;
import com.google.common.collect.ImmutableList;
import com.kennedyrobotics.vision.Vision2016;
import com.ni.vision.NIVision;
import com.ni.vision.NIVision.ColorMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;

@Process("Vision2016")
public class VisionProcessWrapper implements ImageProcess {

	private final Logger log = LogManager.getLogger();
	
	private Vision2016 vision;
	private Image processedFrame = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
	
	@Override
	public void init() {
		
		ConfigWrapperStore.initStore(Vision.getRuntime());
		ConfigWrapper config = ConfigWrapperStore.get().get(ConfigStore.INSTANCE);
		if (config == null) {
			// Create a default config, since one does not exists.
			log.info("No 2016 config found, creating default");
			config = ConfigWrapperStore.get().create(ConfigStore.INSTANCE);
		}
		
		config.apply();
		
		vision = new Vision2016(config.config);
		vision.init();
		if(Vision.getRuntime() instanceof GUIProvider) {
			log.info("Adding to config menu");
			GUIProvider provider = (GUIProvider) Vision.getRuntime();
			List<ConfigPopup> popups = ImmutableList.of(new Vision2016ConfigPopup(config));
			provider.getUI().addConfigMenuItems(popups);
		}
	}

	
	@Override
	public void deinit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void process(Image image) {
		NIVision.imaqDuplicate(processedFrame, image);
		vision.process(image);
		vision.draw(processedFrame);
	}

	private final Image binRgb = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
	
	@Override
	public void update(FrameUpdatable updatable) {
		//log.info("Updating");
		updatable.updateProcessed(processedFrame);
		NIVision.imaqSetImageSize(binRgb, 640, 480);
		NIVision.imaqReplaceColorPlanes(binRgb, binRgb, ColorMode.RGB, vision.getDebugFrame(), vision.getDebugFrame(), vision.getDebugFrame());
		updatable.updateDebug(binRgb);
	}
	
}
