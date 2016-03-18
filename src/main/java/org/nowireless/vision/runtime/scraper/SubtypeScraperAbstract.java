package org.nowireless.vision.runtime.scraper;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class SubtypeScraperAbstract {

	private final Reflections r;
	
	public SubtypeScraperAbstract(List<String> packages) {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		Set<URL> urls = new HashSet<URL>();
		
		for(String packageName : packages) {
			urls.addAll(ClasspathHelper.forPackage(packageName));
		}
		
		builder.addUrls(urls);
		r = new Reflections(builder);
	}
	
	protected <T> Set<Class<? extends T>> getSubtypes(Class<T> clazz) {
		return r.getSubTypesOf(clazz);
	}
	
}
