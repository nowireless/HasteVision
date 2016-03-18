package org.nowireless.vision.runtime.scraper.process;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nowireless.vision.api.process.ImageProcess;
import org.nowireless.vision.api.process.annotations.Process;
import org.nowireless.vision.runtime.scraper.SubtypeScraperAbstract;

import com.google.common.collect.ImmutableList;

public class ProcessScraper extends SubtypeScraperAbstract {

	private final Logger log = LogManager.getLogger();
	
	public ProcessScraper(List<String> packages) {
		super(packages);
	}
	
	public List<Class<? extends ImageProcess>> getAvailableProcesses() {
		List<Class<? extends ImageProcess>> processes = new ArrayList<Class<? extends ImageProcess>>();
		Set<Class<? extends ImageProcess>> clazzes = this.getSubtypes(ImageProcess.class);
		for(Class<? extends ImageProcess> clazz : clazzes) {
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
			
			Process p = clazz.getDeclaredAnnotation(Process.class);
			if(p == null) {
				log.warn("{}, No annoation present", clazz.getName());
				continue;
			} else {
				log.info("Annotation {}", p.value());
			}
			
			processes.add(clazz);
		}
		
		return ImmutableList.copyOf(processes);
	}

}
