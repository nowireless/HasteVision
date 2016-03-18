package org.nowireless.vision.runtime.scraper.imagedriver;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nowireless.vision.api.imagedriver.ImageDriver;
import org.nowireless.vision.api.imagedriver.annotation.IDriver;
import org.nowireless.vision.runtime.scraper.SubtypeScraperAbstract;

import com.google.common.collect.ImmutableList;

public class ImageDriverScraper extends SubtypeScraperAbstract {

	private final Logger log = LogManager.getLogger();
	
	public ImageDriverScraper(List<String> packages) {
		super(packages);
	}

	public List<Class<? extends ImageDriver>> getAvaiableDrivers() {
		List<Class<? extends ImageDriver>> drivers = new ArrayList<Class<? extends ImageDriver>>();
		Set<Class<? extends ImageDriver>> clazzes = this.getSubtypes(ImageDriver.class);
		for(Class<? extends ImageDriver> clazz : clazzes) {
			//Test for good constructor
			boolean good = false;
			for(Constructor<?> con : Arrays.asList(clazz.getConstructors())) {
				if(con.getParameterTypes().length == 0) {
					good = true;
				}
			}
			
			if(!good) {
				log.warn("{}, has a bad constructor", clazz.getName());
				continue;
			}
			
			IDriver d = clazz.getDeclaredAnnotation(IDriver.class);
			if(d == null) {
				log.warn("{}, No annoation present", clazz.getName());
				continue;
			} else {
				log.info("Annotation on {}", clazz);
			}

			
			drivers.add(clazz);
		}

		return ImmutableList.copyOf(drivers);
	}
	
}
