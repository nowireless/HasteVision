package org.nowireless.vision.runtime.gui.simple;

import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nowireless.common.gui.Swing;
import org.nowireless.vision.api.VisionRuntime;
import org.nowireless.vision.api.fps.FPSSource;
import org.nowireless.vision.api.gui.ConfigPopup;
import org.nowireless.vision.api.gui.GUI;
import org.nowireless.vision.api.gui.PauseButtonListener;
import org.nowireless.vision.api.process.ImageProcess;
import org.nowireless.vision.api.statusupdater.StatusUpdater;
import org.nowireless.vision.runtime.gui.simple.SimpleFrame.FrameSelection;

import com.ni.vision.NIVision.Image;

public class SimpleGUI implements GUI {
	
	private transient SimpleFrame frame;
	private transient final Logger log = LogManager.getLogger(this.getClass().getSimpleName());
	private transient ActionListener pauseListener;
	
	public SimpleGUI() {
		this(null);
	}
	
	public SimpleGUI(ActionListener pause) {
		this.pauseListener = pause;
	}
	
	@Override
	public Logger log() {
		return log;
	}

	@Override
	public boolean isReady() {
		if(frame == null) return false;
		return frame.ready;
	}

	@Override
	public JPanel getContentPane() {
		return frame.contentPane;
	}
	
	//
	// All below need to be thread safe-ish
	//
	
	/**
	 * @wbp.parser.entryPoint
	 */
	
	@Override
	public void init() {
		log().info("Preparing UI");
		EventQueue.invokeLater(this);
		while(!this.isReady()) {
			log().info("Wating for UI to be ready");
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		log().info("UI is ready");
	}

	@Override
	public void postInit() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override public void run() { frame.initDialogs(); }
		});
	}
	
	
	@Override
	public void deinit() {
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void run() {
		log().info("Setting system look and feel");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			log().fatal("Could not set system look and feel");
			e.printStackTrace();
		}
		this.setFrame(new SimpleFrame(pauseListener));
	}

	public void setFrame(SimpleFrame frame) {
		this.frame = frame;
	}
	
	public SimpleFrame getFrame() {
		return frame;
	}
	
	@Override
	public void updateSource(Image image) {
		if(frame.getFrameSelection().equals(FrameSelection.SOURCE)) {
			frame.getVideoPanel().updateImage(image);
		}
	}
	@Override
	public void updateDebug(Image image) {
		//log.trace("Updating Debug");
		if(frame.getFrameSelection().equals(FrameSelection.DEBUG)) {
			frame.getVideoPanel().updateImage(image);
		}
	}

	@Override
	public void updateProcessed(Image image) {
		if(frame.getFrameSelection().equals(FrameSelection.PROCESSED)) {
			frame.getVideoPanel().updateImage(image);
		}
	}

	@Override
	public void setPauseListener(PauseButtonListener listener) {
		this.pauseListener = listener;
	}
	
	public ActionListener getPauseListener() {
		return pauseListener;
	}
	
	@Override
	public void updateStatusUpdater(StatusUpdater updater) {
		if(updater != null) {
			frame.setConnected(updater.isConnected());
		} else {
			frame.setConnected(false);
		}
	}
	
	@Override
	public void updateFps(FPSSource source) {
		frame.setFPS(source.getFPS());
	}

	@Override
	public void updateFromProcess(ImageProcess process) {
	}
	
	@Override
	public void updateFromeRuntime(VisionRuntime rt) {
		frame.setPaused(rt.isPaused());
	}
	
	@Swing
	@Override
	public void addConfigMenuItems(List<ConfigPopup> popups) {
		frame.addProcessesConfigMenuItems(popups);
	}

}
