package com.kennedyrobotics.vision;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.kennedyrobotics.vision.Vision2016;
import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;

public class Vision2016Test {

	@Ignore
	@Test
	public void scoreTest() throws IOException {
		List<ParticleReport> reports = ParticleReportUtil.load(ParticleReportUtil.PARTICLE_DATA);
		
		Vision2016 vision = makeTestVision();
		vision.score(reports);
		
		assertTrue(vision.isTargetPresent());
		
	}
	
	@Test
	public void nullReport() {
		Vision2016 vision = makeTestVision();
		vision.score(null);
		assertFalse(vision.isTargetPresent());
	}
	
	@Test
	public void processTest() {
		Vision2016 vision = makeTestVision();
		vision.init();
		Image image = readImage("src/test/resources/211.jpg");
		long now = System.currentTimeMillis();
		vision.process(image);
		
		System.out.println("Time: " + (System.currentTimeMillis() - now));
		assertTrue(vision.isTargetPresent());
	}
	
	/**
	 * Creates a instance of vision2016 for testing.
	 * @return
	 */
	private static Vision2016 makeTestVision() {
		Config config = new Config();
		config.setRageMin(98, 215, 198);
		config.setRageMax(123, 255, 255);
		config.areaScoreMin = 70;
		config.aspectRatioScoreMin = 70;
		
		return new Vision2016(config);
	}
	
	private static Image readImage(String path) {
		Image ret = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
		NIVision.imaqReadFile(ret, path);
		return ret;
	}
}
