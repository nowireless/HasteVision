package org.nowireless.vision.runtime.nt.config;

import org.nowireless.configstore.Config;
import org.nowireless.vision.runtime.nt.NT.NTType;

public class NTConfig extends Config<NTConfig> {

	public NTType type = NTType.CLIENT;
	public String host = "localhost";
	
}
