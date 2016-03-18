package org.nowireless.vision.runtime.config;

import org.nowireless.configstore.Config;

public class VisionRuntimeConfig extends Config<VisionRuntimeConfig> {
		
	public boolean useNT = false;
	
	private String processClassName;
	public String getProcessClassName() {
		return processClassName;
	}
	public void setProcessesClassName(String name) {
		this.processClassName = name;
		this.changed();
	}

	private String imageDriverClassName;
	public String getImageDriverClassName() {
		return imageDriverClassName;
	}
	public void setImageDriverClassName(String name) {
		this.imageDriverClassName = name;
		this.changed();
	}	
}
