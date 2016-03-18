package org.nowireless.vision.api.gui;

import java.util.List;

import javax.swing.JPanel;

import org.nowireless.common.Initializable;
import org.nowireless.common.LoggerProvidor;
import org.nowireless.common.gui.Swing;
import org.nowireless.vision.api.FrameUpdatable;
import org.nowireless.vision.api.VisionRuntime;
import org.nowireless.vision.api.fps.FPSSource;
import org.nowireless.vision.api.process.ImageProcess;
import org.nowireless.vision.api.statusupdater.StatusUpdater;

public interface GUI extends Initializable, LoggerProvidor, Runnable, FrameUpdatable {

	public boolean isReady();
	
	public JPanel getContentPane();
	
	public void setPauseListener(PauseButtonListener listener);
	
	public void updateStatusUpdater(StatusUpdater updater);
	
	public void updateFps(FPSSource source);
	
	public void updateFromeRuntime(VisionRuntime rt);
	
	public void updateFromProcess(ImageProcess process);
	
	public void postInit();
	
	@Swing public void addConfigMenuItems(List<ConfigPopup> popups);
}
