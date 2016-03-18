package com.kennedyrobotics.vision.pc;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nowireless.configstore.ConfigStore;
import org.nowireless.mstore.MStoreUser;

public class ConfigWrapperStore extends ConfigStore<ConfigWrapper> {
	private static Logger LOG = LogManager.getLogger("ImgProcCS");
	private static ConfigWrapperStore i = null;
	public static ConfigWrapperStore get() { return i; }
	public static void initStore(MStoreUser user) {
		LOG.info("Initializing");
		Validate.notNull(user);
		i = new ConfigWrapperStore(user);
		i.init();
	}

	protected ConfigWrapperStore(MStoreUser user) {
		super("2016Vision", ConfigWrapper.class, user);
	}

}
