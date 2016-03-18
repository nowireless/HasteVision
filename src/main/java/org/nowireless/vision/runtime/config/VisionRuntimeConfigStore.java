package org.nowireless.vision.runtime.config;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nowireless.configstore.ConfigStore;
import org.nowireless.mstore.MStoreUser;

public class VisionRuntimeConfigStore extends ConfigStore<VisionRuntimeConfig> {
	
	private static Logger LOG = LogManager.getLogger("VRCS");
	private static VisionRuntimeConfigStore i = null;
	public static void initStore(MStoreUser user) {
		LOG.info("Initializing");
		Validate.notNull(user);
		i = new VisionRuntimeConfigStore(user);
		i.init();
	}
	
	public static VisionRuntimeConfigStore get() {
		return i;
	}
	
	protected VisionRuntimeConfigStore(MStoreUser user) {
		super("visionRuntimeConfig", VisionRuntimeConfig.class, user);
	}

}
