package org.nowireless.vision.runtime.standalone;

import org.nowireless.common.util.ShutDownHookAbstract;
import org.nowireless.vision.api.gui.GUI;
import org.nowireless.vision.api.gui.GUIProvider;
import org.nowireless.vision.runtime.VisionRuntimeAbstract;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;

public class StandaloneVisionEngine extends VisionRuntimeAbstract implements GUIProvider {

//	private static final Mat OVER_DUE = Util.makeTextImage("Over Due");
	
	private class StandaloneShutDownHook extends ShutDownHookAbstract {

		@Override
		public void run() {
			try {
				StandaloneVisionEngine.this.stopEngineAndJoin();
			} catch (InterruptedException e) {
				log().warn("Could not Stop and join with engine");
				e.printStackTrace();
			}
		}
	}
	
	private transient final GUI ui;
	
	@Override public GUI getUI() { return ui; }
	
	public StandaloneVisionEngine(GUI ui) {
		super(100, "Vision");
		this.ui = ui;
	}
	
	public StandaloneVisionEngine() {
		this(null);
	}
	
	@Override
	public void init() {
		this.getWatcher().disable();
		if(ui != null) ui.init();
		this.getWatcher().enable();
		
		super.init();
		
		ui.postInit();
		
		new StandaloneShutDownHook().register();
	}
	
	@Override
	public void deinit() {
		if(ui != null) ui.deinit();
		super.deinit();
	}
	
	@Override
	protected void onPause() {
	}

	@Override
	protected void onPostProccess(Image source) {
	}

	@Override
	protected void onLoop() {
		if(ui != null && ui.isReady()) {
			
			ui.updateStatusUpdater(this.getUpdater());
			ui.updateFps(this);
			ui.updateFromProcess(getProcess());
			
//			Mat temp = new Mat();
			Image temp = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
			
			this.getProcessedFrame(temp);
			ui.updateProcessed(temp);
			
			this.getSourceFrame(temp);
			ui.updateSource(temp);
			
			this.getDebugFrame(temp);
			ui.updateDebug(temp);
			
			temp.free();
			ui.updateFromeRuntime(this);
		}
	}

	@Override
	protected void overdue() {
		if(ui != null && ui.isReady()) {
//			ui.updateSource(OVER_DUE);
//			ui.updateDebug(OVER_DUE);
//			ui.updateProcessed(OVER_DUE);
		}
	}
	
}
