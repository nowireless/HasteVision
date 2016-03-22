package com.kennedyrobotics.vision;

import com.ni.vision.NIVision.Range;

	/**
	 * {@link Vision2016} configuration
	 * @author Ryan
	 *
	 */
	public class Config {
		
		//Set the visoin values here:
		public static final int DEFAULT_MIN_H = 0;
		public static final int DEFAULT_MIN_S = 0;
		public static final int DEFAULT_MIN_V = 0;
		public static final int DEFAULT_MAX_H = 255;
		public static final int DEFAULT_MAX_S = 255;
		public static final int DEFAULT_MAX_V = 255;
		public static final double DEFAULT_AREA_SCORE = 70;
		public static final double DEFAULT_ASPECT_SCORE = 70;
		
		final Range hueRange = new Range();
		final Range saturationRange = new Range();
		final Range valueRange = new Range();
		
		public double areaScoreMin = DEFAULT_AREA_SCORE;
		public double aspectRatioScoreMin = DEFAULT_ASPECT_SCORE;
		
		public Config() {
			this.setRageMin(DEFAULT_MIN_H, DEFAULT_MIN_S, DEFAULT_MIN_V);
			this.setRageMax(DEFAULT_MAX_H, DEFAULT_MAX_S, DEFAULT_MAX_H);
		}
		
		public void setRageMin(int h, int s, int v) {
			hueRange.minValue = h;
			saturationRange.minValue = s;
			valueRange.minValue = v;

			hueRange.write();
			saturationRange.write();
			valueRange.write();
		}
		
		public void setRageMax(int h, int s, int v) {
			hueRange.maxValue = h;
			saturationRange.maxValue = s;
			valueRange.maxValue = v;
			
			hueRange.write();
			saturationRange.write();
			valueRange.write();
		}
		
	}
