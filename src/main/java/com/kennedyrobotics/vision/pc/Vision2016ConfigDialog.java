package com.kennedyrobotics.vision.pc;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nowireless.vision.api.Vision;

import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

@SuppressWarnings("serial")
public class Vision2016ConfigDialog extends JDialog {

	private class ConfigUpdater implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent arg0) {
			config.lockConfig();
			
			log.info("Updating config");
			
			
			config.minH = ((int)lowHSpinner.getValue());
			config.minS = ((int)lowSSpinner.getValue());
			config.minV = ((int)lowVSpinner.getValue());
			config.maxH = ((int)highHSpinner.getValue());
			config.maxS = ((int)highSSpinner.getValue());
			config.maxV = ((int)highVSpinner.getValue());
			
			config.area = ((int)areaScoreSpinner.getValue());
			config.aspect = ((int)aspectScoreSpinner.getValue());
			
			config.changed();
			
			config.unlockConfig();
			
//			log.info("Low {}, High {}", low, high);
			log.info("Cueing a reprocess");
			Vision.getRuntime().cueImageProcess();
		}
		
	}
	
	private class OkButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Vision2016ConfigDialog.this.setVisible(false);
		}
	}
	
	private class CancelButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Vision2016ConfigDialog.this.setVisible(false);
		}
	}
	
	private final JPanel contentPanel = new JPanel();
	private final Logger log = LogManager.getLogger();
	
	private final JSpinner lowHSpinner;
	private final JSpinner lowSSpinner;
	private final JSpinner lowVSpinner;
	private final JSpinner highHSpinner;
	private final JSpinner highSSpinner;
	private final JSpinner highVSpinner;
	private final JSpinner areaScoreSpinner;
	private final JSpinner aspectScoreSpinner;
	
	private final ConfigWrapper config;
	
	/**
	 * Create the dialog.
	 */
	public Vision2016ConfigDialog(ConfigWrapper config) {
		this.config = Validate.notNull(config);
		
		this.config.lockConfig();
		
		int minH = config.minH;
		int minS = config.minS;
		int minV = config.minV;
		
		int maxH = config.maxH;
		int maxS = config.maxS;
		int maxV = config.maxV;
		
		int area = (int) config.area;
		int aspect = (int) config.aspect;
		
		this.config.unlockConfig();
		
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);

		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setAlwaysOnTop(true);
		
		JLabel lblLowThreshold = new JLabel("Low Threshold");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblLowThreshold, 10, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblLowThreshold, 10, SpringLayout.WEST, contentPanel);
		contentPanel.add(lblLowThreshold);
		
		JLabel lblH = new JLabel("H:");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblH, 8, SpringLayout.SOUTH, lblLowThreshold);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblH, 0, SpringLayout.WEST, lblLowThreshold);
		contentPanel.add(lblH);
		
		lowHSpinner = new JSpinner();
		lowHSpinner.setModel(new SpinnerNumberModel(minH, 0, 255, 1));
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lowHSpinner, 6, SpringLayout.SOUTH, lblLowThreshold);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lowHSpinner, 27, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, lowHSpinner, 50, SpringLayout.EAST, lblH);
		contentPanel.add(lowHSpinner);
		
		JLabel lblS = new JLabel("S:");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblS, 8, SpringLayout.SOUTH, lblLowThreshold);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblS, 6, SpringLayout.EAST, lowHSpinner);
		contentPanel.add(lblS);
		
		lowSSpinner = new JSpinner();
		lowSSpinner.setModel(new SpinnerNumberModel(minS, 0, 255, 1));
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lowSSpinner, -2, SpringLayout.NORTH, lblH);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lowSSpinner, 6, SpringLayout.EAST, lblS);
		sl_contentPanel.putConstraint(SpringLayout.EAST, lowSSpinner, 50, SpringLayout.EAST, lblS);
		contentPanel.add(lowSSpinner);
		
		JLabel lblV = new JLabel("V:");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblV, 0, SpringLayout.NORTH, lblH);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblV, 6, SpringLayout.EAST, lowSSpinner);
		contentPanel.add(lblV);
		
		lowVSpinner = new JSpinner();
		lowVSpinner.setModel(new SpinnerNumberModel(minV, 0, 255, 1));
		sl_contentPanel.putConstraint(SpringLayout.WEST, lowVSpinner, 6, SpringLayout.EAST, lblV);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, lowVSpinner, 0, SpringLayout.SOUTH, lowHSpinner);
		sl_contentPanel.putConstraint(SpringLayout.EAST, lowVSpinner, 50, SpringLayout.EAST, lblV);
		contentPanel.add(lowVSpinner);
		
		JLabel lblHighThreshold = new JLabel("High Threshold");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblHighThreshold, 6, SpringLayout.SOUTH, lblH);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblHighThreshold, 0, SpringLayout.WEST, lblLowThreshold);
		contentPanel.add(lblHighThreshold);
		
		JLabel lblH_1 = new JLabel("H:");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblH_1, 8, SpringLayout.SOUTH, lblHighThreshold);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblH_1, 0, SpringLayout.WEST, lblLowThreshold);
		contentPanel.add(lblH_1);
		
		highHSpinner = new JSpinner();
		highHSpinner.setModel(new SpinnerNumberModel(maxH, 0, 255, 1));
		sl_contentPanel.putConstraint(SpringLayout.NORTH, highHSpinner, 6, SpringLayout.SOUTH, lblHighThreshold);
		sl_contentPanel.putConstraint(SpringLayout.WEST, highHSpinner, 0, SpringLayout.WEST, lowHSpinner);
		sl_contentPanel.putConstraint(SpringLayout.EAST, highHSpinner, 50, SpringLayout.EAST, lblH_1);
		contentPanel.add(highHSpinner);
		
		JLabel lblS_1 = new JLabel("S:");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblS_1, 8, SpringLayout.SOUTH, lblHighThreshold);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblS_1, 0, SpringLayout.WEST, lblS);
		contentPanel.add(lblS_1);
		
		highSSpinner = new JSpinner();
		highSSpinner.setModel(new SpinnerNumberModel(maxS, 0, 255, 1));
		sl_contentPanel.putConstraint(SpringLayout.NORTH, highSSpinner, -2, SpringLayout.NORTH, lblH_1);
		sl_contentPanel.putConstraint(SpringLayout.WEST, highSSpinner, 0, SpringLayout.WEST, lowSSpinner);
		sl_contentPanel.putConstraint(SpringLayout.EAST, highSSpinner, 50, SpringLayout.EAST, lblS_1);
		contentPanel.add(highSSpinner);
		
		JLabel lblV_1 = new JLabel("V:");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblV_1, 0, SpringLayout.NORTH, lblH_1);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblV_1, 0, SpringLayout.WEST, lblV);
		contentPanel.add(lblV_1);
		
		highVSpinner = new JSpinner();
		highVSpinner.setModel(new SpinnerNumberModel(maxV, 0, 255, 1));
		sl_contentPanel.putConstraint(SpringLayout.NORTH, highVSpinner, -2, SpringLayout.NORTH, lblH_1);
		sl_contentPanel.putConstraint(SpringLayout.WEST, highVSpinner, 4, SpringLayout.EAST, lblV_1);
		sl_contentPanel.putConstraint(SpringLayout.EAST, highVSpinner, 50, SpringLayout.EAST, lblV_1);
		contentPanel.add(highVSpinner);
		
		JLabel lblAspectScoreMin = new JLabel("Aspect Score Min");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblAspectScoreMin, 10, SpringLayout.SOUTH, highHSpinner);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblAspectScoreMin, 0, SpringLayout.WEST, lblLowThreshold);
		contentPanel.add(lblAspectScoreMin);
		
		aspectScoreSpinner = new JSpinner();
		aspectScoreSpinner.setModel(new SpinnerNumberModel(aspect, 0, 100, 1));
		sl_contentPanel.putConstraint(SpringLayout.NORTH, aspectScoreSpinner, 10, SpringLayout.SOUTH, highSSpinner);
		sl_contentPanel.putConstraint(SpringLayout.WEST, aspectScoreSpinner, 0, SpringLayout.EAST, lblAspectScoreMin);
		sl_contentPanel.putConstraint(SpringLayout.EAST, aspectScoreSpinner, 0, SpringLayout.EAST, lowSSpinner);
		contentPanel.add(aspectScoreSpinner);
		
		JLabel lblAreaScoreMin = new JLabel("Area Score Min");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblAreaScoreMin, 14, SpringLayout.SOUTH, lblAspectScoreMin);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblAreaScoreMin, 0, SpringLayout.WEST, lblLowThreshold);
		contentPanel.add(lblAreaScoreMin);
		
		areaScoreSpinner = new JSpinner();
		areaScoreSpinner.setModel(new SpinnerNumberModel(area, 0, 100, 1));
		sl_contentPanel.putConstraint(SpringLayout.NORTH, areaScoreSpinner, 6, SpringLayout.SOUTH, aspectScoreSpinner);
		sl_contentPanel.putConstraint(SpringLayout.WEST, areaScoreSpinner, 0, SpringLayout.WEST, lowSSpinner);
		sl_contentPanel.putConstraint(SpringLayout.EAST, areaScoreSpinner, 0, SpringLayout.EAST, lowSSpinner);
		contentPanel.add(areaScoreSpinner);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(new OkButtonListener());
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(new CancelButtonListener());
				buttonPane.add(cancelButton);
			}
		}
		
		ConfigUpdater updater = new ConfigUpdater();
		lowHSpinner.addChangeListener(updater);
		lowSSpinner.addChangeListener(updater);
		lowVSpinner.addChangeListener(updater);
		highHSpinner.addChangeListener(updater);
		highSSpinner.addChangeListener(updater);
		highVSpinner.addChangeListener(updater);
		areaScoreSpinner.addChangeListener(updater);
		aspectScoreSpinner.addChangeListener(updater);
	}
}
