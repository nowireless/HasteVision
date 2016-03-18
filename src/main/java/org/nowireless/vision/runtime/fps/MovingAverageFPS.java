package org.nowireless.vision.runtime.fps;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nowireless.vision.api.fps.FPS;

public class MovingAverageFPS implements FPS {

	private static final int kSamples = 5;

	private final Logger log = LogManager.getLogger(this.getClass().getSimpleName());
	private final int size;
	private final List<Long> samples;
	private volatile double average;
	private long lastCheckIn;
	
	public MovingAverageFPS() {
		this(kSamples);
	}
	
	public MovingAverageFPS(int sampleSize) {
		this.size = sampleSize;
		this.samples = new ArrayList<Long>();
		this.reset();
	}
	
	@Override
	public double getFPS() {
		return average;
	}

	@Override
	public void update() {
		long current = System.currentTimeMillis();
		
		samples.remove(0);
		long delta = current - this.lastCheckIn;
		samples.add(delta);
		
		long total = 0;
		for(int i = 0; i < size; i++) {
			long sample = samples.get(i);
			total += sample;
		}
		
		if(total != 0) {
			double frameTime = (total / size);
			frameTime = frameTime / 1000;
			this.average = 1 / frameTime;
			
			log.trace("Delta {}, Samples {}, Frame Time {}, Avg {}", delta, samples, frameTime, average);
			
		} else {
			this.average = 0;
		}
		
		this.lastCheckIn = current;
	}

	@Override
	public void reset() {
		log.trace("Resting FPS");
		this.lastCheckIn = System.currentTimeMillis();
		samples.clear();
		for(int i = 0; i < size; i++) {
			samples.add(Long.valueOf(0));
		}
	}
}
