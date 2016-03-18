package org.nowireless.vision.runtime.standalone;

import org.nowireless.vision.api.VisionRuntime;
import org.nowireless.vision.api.gui.GUI;
import org.nowireless.vision.api.gui.PauseButtonListener;
import org.nowireless.vision.loader.VisionLoaderAbstract;
import org.nowireless.vision.runtime.gui.simple.SimpleGUI;

public class StandaloneVisionLoader extends VisionLoaderAbstract {

	public void start() {
		this.load().startEngine();
	}
	
	@Override
	public VisionRuntime load() {
		return super.load();
	}
	
	@Override
	protected final VisionRuntime getEngine() {
		GUI ui = this.getUI();
		StandaloneVisionEngine engine = new StandaloneVisionEngine(ui);
		ui.setPauseListener(new PauseButtonListener(engine));
		return engine;
	}
	
	
	protected GUI getUI() {
		return new SimpleGUI();
	}
	
}
