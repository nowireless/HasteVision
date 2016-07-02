package com.kennedyrobotics.vision.pc.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.SpringLayout;
import javax.swing.JLabel;

public class DebugView extends JFrame {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DebugView frame = new DebugView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DebugView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		JPanel panel = new JPanel();
		JScrollPane pane = new JScrollPane(panel);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);
		
		DebugEntry distnace = new DebugEntry("Distnace ft");
		sl_panel.putConstraint(SpringLayout.NORTH, distnace , 10, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.WEST, distnace , 10, SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.SOUTH, distnace , 50, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.EAST, distnace , -10, SpringLayout.EAST, panel);
		panel.add(distnace );
		
		DebugEntry debugEntry = new DebugEntry("Target turn to angle");
		sl_panel.putConstraint(SpringLayout.NORTH, debugEntry, 0, SpringLayout.SOUTH, distnace);
		sl_panel.putConstraint(SpringLayout.WEST, debugEntry, 10, SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.SOUTH, debugEntry, 40, SpringLayout.SOUTH, distnace);
		sl_panel.putConstraint(SpringLayout.EAST, debugEntry, -10, SpringLayout.EAST, panel);
		panel.add(debugEntry);
		
		
		this.setContentPane(pane);
		
	}
}
