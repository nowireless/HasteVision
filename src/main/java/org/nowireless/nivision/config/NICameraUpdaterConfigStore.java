package org.nowireless.nivision.config;

import org.nowireless.configstore.ConfigStore;
import org.nowireless.vision.api.Vision;

public class NICameraUpdaterConfigStore extends ConfigStore<NICameraUpdaterConfig> {

	public NICameraUpdaterConfigStore() {
		super("ni_camera_updater", NICameraUpdaterConfig.class, Vision.getRuntime());
		// TODO Auto-generated constructor stub
	}

}
