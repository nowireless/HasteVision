package org.nowireless.vision.runtime.nt;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nowireless.common.Initializable;
import org.nowireless.configstore.ConfigStore;
import org.nowireless.vision.api.Vision;
import org.nowireless.vision.runtime.nt.config.NTConfig;
import org.nowireless.vision.runtime.nt.config.NTConfigStore;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 * Initializes {@link NetworkTable} for use with the {@link VisionRuntime}
 * @author Ryan
 *
 */
public class NT implements Initializable {
	
	public enum NTType {
		SERVER,
		CLIENT
	}
	
	private NTConfig config = null;
	private final Logger log = LogManager.getLogger();
	
	public NT() {}
	
	public NT(NTConfig config) {
		this.config = Validate.notNull(config);
	}
	
	
	
	@Override
	public void init() {
		
		NTConfigStore.initStore(Vision.getRuntime());
		NTConfig ntConfig = NTConfigStore.get().get(ConfigStore.INSTANCE);
		if(ntConfig == null) {
			ntConfig = NTConfigStore.get().create(ConfigStore.INSTANCE);
		}
		
		this.config = Validate.notNull(ntConfig);
		
		if(NTType.SERVER.equals(this.config.type)) {
			NetworkTable.setServerMode();
		} else if(NTType.CLIENT.equals(this.config.type)) {
			NetworkTable.setClientMode();
			NetworkTable.setIPAddress(this.config.host);
		} else {
			log.fatal("Invalid NT type");
			return;
		}
		NetworkTable.initialize();

	}
	
	@Override
	public void deinit() {
	}
}
