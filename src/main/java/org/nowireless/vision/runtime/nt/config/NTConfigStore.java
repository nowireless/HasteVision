package org.nowireless.vision.runtime.nt.config;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nowireless.configstore.ConfigStore;
import org.nowireless.mstore.MStoreUser;

public class NTConfigStore extends ConfigStore<NTConfig> {

	private static Logger LOG = LogManager.getLogger();
	private static NTConfigStore i = null;
	
	protected NTConfigStore(MStoreUser user) {
		super("NT", NTConfig.class, user);
	}
	
	public static void initStore(MStoreUser user) {
		LOG.info("Initializing");
		Validate.notNull(user);
		i = new NTConfigStore(user);
		i.init();
	}
	
	public static NTConfigStore get() {
		return i;
	}

}
