package org.nowireless.vision.runtime.gui.simple;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nowireless.common.gui.JBooleanBox;
import org.nowireless.vision.api.Vision;
import org.nowireless.vision.api.gui.ConfigPopup;
import org.nowireless.vision.runtime.VisionRuntimeAbstract;
import org.nowireless.vision.runtime.config.VisionRuntimeConfig;
import org.nowireless.vision.runtime.gui.ImageDriverSelectorDialog;
import org.nowireless.vision.runtime.gui.JVideoPanel;
import org.nowireless.vision.runtime.gui.ProcessSelectorDialog;

import javax.swing.JToggleButton;

import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Font;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import java.awt.event.ActionEvent;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class SimpleFrame extends JFrame {
	
	public enum FrameSelection {
		SOURCE,
		PROCESSED,
		DEBUG
	}
	
	private class FPSUpdate implements Runnable {
		
		@Override
		public void run() {
			SimpleFrame.this.fps.setText(Long.toString(Math.round(currentFps)));
		}		
	}
	
	private class FrameButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(rdbtnmntmSource.isSelected()) {
				frameSelection = FrameSelection.SOURCE;
			} else if(rdbtnmntmProcessed.isSelected()) {
				frameSelection = FrameSelection.PROCESSED;
			} else {
				frameSelection = FrameSelection.DEBUG;
			}
		}
		
	}
	
	
	private class MenuConfigImageProcessSelectionListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(processDialog != null) {
				log.info("Setting process dialog to visible");
				processDialog.setAlwaysOnTop(true);
				processDialog.setVisible(true);
			}
		}
		
	}
	
	private class MenuConfigImageDriverSelectionListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			log.info("Setting Image driver dialog to visible");
			if(imageDriverDialog != null) {
				imageDriverDialog.setAlwaysOnTop(true);
				imageDriverDialog.setVisible(true);
			}
		}
		
	}
	
	private class PauseUpdater implements Runnable {
				
		@Override
		public void run() {
//			mnConfig.setEnabled(paused);
			btnSaveSourceImage.setEnabled(paused);
			
		}
	}
	
	private final Logger log = LogManager.getLogger();
	
	transient volatile boolean ready = false;
	transient final JPanel contentPane;
	private transient final JVideoPanel videoPanel;
	private transient final JBooleanBox booleanBox;
	private transient final JLabel fps;
	private transient volatile double currentFps = 0;
	private transient volatile boolean paused = false;
	private final FPSUpdate fpsUpdater = new FPSUpdate();
	private final SpringLayout sl_contentPane;
	
	private final JRadioButtonMenuItem rdbtnmntmSource;
	private final JRadioButtonMenuItem rdbtnmntmProcessed;
	private final JRadioButtonMenuItem mntmDebug;
	
	private final JMenu mnConfig;
	private final JMenu mnView;
	
	private ProcessSelectorDialog processDialog;
	private ImageDriverSelectorDialog imageDriverDialog;
	private final PauseUpdater pauseUpdater = new PauseUpdater();
	
	private FrameSelection frameSelection = FrameSelection.PROCESSED;
	private JMenuItem mntmImageProcessSelection;
	private JMenuItem mntmImageDriverSelection;
	public FrameSelection getFrameSelection() {
		return this.frameSelection;
	}
	
	public JVideoPanel getVideoPanel() { return videoPanel; }
	public JBooleanBox getStatusUpdaterBooleanBox() { return booleanBox; }
	public JLabel getFpsLabel() { return fps; }
	public JPanel getMainContentPane() { return contentPane; }
	public SpringLayout getMainContentPaneLayout() { return sl_contentPane; }
	
	JButton btnSaveSourceImage;
	
	public SimpleFrame(){
		this(null);
	}
	
	public SimpleFrame(ActionListener pause) {
		super("Team 3081 RoboEagles Vision");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 686, 620);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFrame = new JMenu("Frame");
		menuBar.add(mnFrame);
		
		rdbtnmntmSource = new JRadioButtonMenuItem("Source");
		rdbtnmntmProcessed = new JRadioButtonMenuItem("Processed");
		mntmDebug = new JRadioButtonMenuItem("Debug");
		
		FrameButtonListener frameButtonListner = new FrameButtonListener();
		rdbtnmntmSource.addActionListener(frameButtonListner);
		rdbtnmntmProcessed.addActionListener(frameButtonListner);
		mntmDebug.addActionListener(frameButtonListner);
		
		ButtonGroup frameButtonGroup = new ButtonGroup();
		frameButtonGroup.add(rdbtnmntmSource);
		frameButtonGroup.add(rdbtnmntmProcessed);
		frameButtonGroup.add(mntmDebug);
		
		
		mnFrame.add(rdbtnmntmSource);
		mnFrame.add(rdbtnmntmProcessed);
		mnFrame.add(mntmDebug);
		
		mnConfig = new JMenu("Config");
		menuBar.add(mnConfig);
		mnConfig.setEnabled(true);
		
		mntmImageProcessSelection = new JMenuItem("Image Process Selection");
		mntmImageProcessSelection.addActionListener(new MenuConfigImageProcessSelectionListener());
		mnConfig.add(mntmImageProcessSelection);
		
		mntmImageDriverSelection = new JMenuItem("Image Driver Selection");
		mntmImageDriverSelection.addActionListener(new MenuConfigImageDriverSelectionListener());
		mnConfig.add(mntmImageDriverSelection);
		
		mnView = new JMenu("View");
		menuBar.add(mnView);
				
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		videoPanel = new JVideoPanel(640, 480);
		sl_contentPane.putConstraint(SpringLayout.NORTH, videoPanel, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, videoPanel, 10, SpringLayout.WEST, contentPane);
		contentPane.add(videoPanel);
		
		JToggleButton tglbtnPause = new JToggleButton("Pause");
		sl_contentPane.putConstraint(SpringLayout.NORTH, tglbtnPause, 6, SpringLayout.SOUTH, videoPanel);
		sl_contentPane.putConstraint(SpringLayout.WEST, tglbtnPause, 0, SpringLayout.WEST, videoPanel);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, tglbtnPause, -6, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, tglbtnPause, 90, SpringLayout.WEST, contentPane);
		tglbtnPause.addActionListener(pause);
		contentPane.add(tglbtnPause);
		
		JLabel lblStatusUpdaterConnected = new JLabel("Status Updater Connected");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblStatusUpdaterConnected, 6, SpringLayout.SOUTH, videoPanel);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblStatusUpdaterConnected, 6, SpringLayout.EAST, tglbtnPause);
		contentPane.add(lblStatusUpdaterConnected);
		
		booleanBox = new JBooleanBox();
		sl_contentPane.putConstraint(SpringLayout.NORTH, booleanBox, 6, SpringLayout.SOUTH, lblStatusUpdaterConnected);
		sl_contentPane.putConstraint(SpringLayout.WEST, booleanBox, 10, SpringLayout.WEST, lblStatusUpdaterConnected);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, booleanBox, -11, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, booleanBox, -10, SpringLayout.EAST, lblStatusUpdaterConnected);
		contentPane.add(booleanBox);
		
		JLabel lblFps = new JLabel("FPS");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblFps, 6, SpringLayout.SOUTH, videoPanel);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblFps, 6, SpringLayout.EAST, lblStatusUpdaterConnected);
		contentPane.add(lblFps);
		
		fps = new JLabel("0");
		sl_contentPane.putConstraint(SpringLayout.NORTH, fps, 0, SpringLayout.NORTH, booleanBox);
		sl_contentPane.putConstraint(SpringLayout.WEST, fps, 15, SpringLayout.EAST, booleanBox);
		fps.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
		fps.setHorizontalAlignment(SwingConstants.LEFT);
		contentPane.add(fps);
		
		btnSaveSourceImage = new JButton("Save Source Image");
		btnSaveSourceImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Vision.getRuntime().cueSaveImageSource();
			}
		});
		
		btnSaveSourceImage.setEnabled(false);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnSaveSourceImage, 6, SpringLayout.SOUTH, videoPanel);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnSaveSourceImage, 0, SpringLayout.EAST, videoPanel);
		contentPane.add(btnSaveSourceImage);
		videoPanel.init();
		
		this.setVisible(true);
		ready = true;
	}
	
	public void initDialogs() {
		if(Vision.getRuntime() instanceof VisionRuntimeAbstract) {
			VisionRuntimeConfig config = ((VisionRuntimeAbstract) Vision.getRuntime()).getConfig();
			processDialog = new ProcessSelectorDialog(config);
			imageDriverDialog = new ImageDriverSelectorDialog(config);
		} else{
			processDialog = null;
			imageDriverDialog = null;
		}
	}

	public void setConnected(boolean state) {
		booleanBox.set(state);;
	}
	
	public void setFPS(double fps) {
		currentFps = fps;
		SwingUtilities.invokeLater(fpsUpdater);
	}
	
	public void setPaused(boolean paused) {
		this.paused = paused;
		SwingUtilities.invokeLater(pauseUpdater);
	}
	
	public void addProcessesConfigMenuItems(List<ConfigPopup> popups) {
		for(ConfigPopup popup : popups) {
			JMenuItem item = new JMenuItem(popup.getName());
			item.addActionListener(popup);
			mnConfig.add(item);
		}
	}
	
	public void addViewMenuItems(List<JMenuItem> items) {
		for(JMenuItem item:items) {
			mnView.add(item);
		}
	}
}