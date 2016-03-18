package com.kennedyrobotics.vision.pc;

import com.kennedyrobotics.vision.Config;

public class ConfigWrapper extends org.nowireless.configstore.Config<ConfigWrapper> {
	
	public transient final com.kennedyrobotics.vision.Config config;
	
	public int minH = Config.DEFAULT_MIN_H;
	public int minS = Config.DEFAULT_MIN_S;
	public int minV = Config.DEFAULT_MIN_V;
	public int maxH = Config.DEFAULT_MAX_H;
	public int maxS = Config.DEFAULT_MAX_S;
	public int maxV = Config.DEFAULT_MAX_V;

	public double area = Config.DEFAULT_AREA_SCORE;
	public double aspect = Config.DEFAULT_ASPECT_SCORE;
	
	public ConfigWrapper() {
		this.config = new Config();
		this.apply();
	}
	
//	@Override
//	public ConfigWrapper load(ConfigWrapper that) {
//		config.setRageMax(that.maxH, that.maxS, that.maxV);
//		config.setRageMin(that.minH, that.minS, that.minV);
//		config.areaScoreMin = that.area;
//		config.aspectRatioScoreMin = that.aspect;
//		return this;
//	}
	
	@Override
	public void changed() {
		this.apply();
		super.changed();
	}
	
	public void apply() {
		config.setRageMax(maxH, maxS, maxV);
		config.setRageMin(minH, minS, minV);
		config.areaScoreMin = area;
		config.aspectRatioScoreMin = aspect;
	}
		
}
