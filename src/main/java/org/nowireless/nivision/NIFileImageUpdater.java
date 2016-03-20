package org.nowireless.nivision;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.nowireless.common.util.TimerDelta;
import org.nowireless.nivision.config.NIFileImageUpdaterConfig;
import org.nowireless.vision.api.imagedriver.annotation.IDriver;
import org.nowireless.vision.runtime.imagedriver.ImageDriverAbstract;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;

@IDriver
public class NIFileImageUpdater extends ImageDriverAbstract {

	//public transient static final String IMAGE_DIR = "./images";
	//public transient static final long DELTA = 200;
	
	public transient final List<Image> images = new ArrayList<Image>();
	private transient int imageNum = 0;
	private transient final TimerDelta timer = new TimerDelta();
	
	private NIFileImageUpdaterConfig config;
	
	public NIFileImageUpdater() {
		super(200, "File Image Updater");
	}

	
	@Override
	public void init() {
		Validate.notNull(this.getUpdateable());
		
		log().info("Working Dir {}", Paths.get(".").toAbsolutePath().normalize().toString());
		
		for(Image image : images) {
			image.free();
		}
		images.clear();

		config = new NIFileImageUpdaterConfig();
		log().info("Looking in dir {} for images", config.imageDir);
		
		File imageDir = new File(config.imageDir);
		imageDir.mkdirs();
		Validate.isTrue(imageDir.isDirectory());
		
		for(String image :  Arrays.asList(imageDir.list())){
			log().info("Found image {}", image);
			if(image.contains(".jpg")) {
				Image img = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
				NIVision.imaqReadFile(img, config.imageDir + "/" + image);
				this.images.add(img);
			} else {
				log().warn("Invalid Image ignoring");
			}

		}
		log().info("Found {} images", images.size());
		imageNum = 0;
		timer.reset();
	}

	@Override
	public void deinit() {
		for(Image image : images) {
			image.free();
		}
//		config.sync();
	}

	@Override
	protected long getSleepTime() {
		return 10;
	}

	@Override
	protected void overdue() {
	}

	@Override
	public void runTask() {
		double delta = timer.delta();
		if (delta > config.delta) {
			log().trace("Updating Image");
			if (!images.isEmpty()) {
				if (imageNum >= images.size()) {
					imageNum = 0;
				}
				log().trace("Updating image to number {}", imageNum);
				this.getUpdateable().updateSource(images.get(imageNum));
				imageNum++;
			} else {
				log().trace("Image List is empty: {}", images.size());
			}
			timer.reset();
		}
		log().trace("Delta {}", delta);
	}

}
