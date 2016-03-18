package org.nowireless.vision.runtime.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nowireless.vision.api.Vision;
import org.nowireless.vision.runtime.config.VisionRuntimeConfig;
import javax.swing.JList;
import javax.swing.SpringLayout;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class ProcessSelectorDialog extends JDialog {

	private class OkButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String selection = list.getSelectedValue();
			if(selection == null) {
				log.warn("No selection was made");
				return;
			}
			
			log.info("Selection {}", selection);
			
			config.lockConfig();
			config.setProcessesClassName(selection);
			config.unlockConfig();
			ProcessSelectorDialog.this.setVisible(false);
		}
	}
	
	private class CancelButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ProcessSelectorDialog.this.setVisible(false);
		}
	}
	
	private final Logger log = LogManager.getLogger();
	private final JPanel contentPanel = new JPanel();
	private final JList<String> list;
	private final VisionRuntimeConfig config;
	/**
	 * Create the dialog.
	 */
	public ProcessSelectorDialog(VisionRuntimeConfig config) {
		this.config = Validate.notNull(config);
		setBounds(100, 100, 530, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);
		
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		
		setAlwaysOnTop(true);
		
		ArrayList<String> clist = new ArrayList<>();
		for(Class<?> c : Vision.getRuntime().getAvailableProcesses()) {
			clist.add(c.getName());
		}
		System.out.println(clist);
		String a[] = new String[clist.size()];
		clist.toArray(a);
		
		list = new JList<String>(a);
		JScrollPane pane = new JScrollPane(list);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, pane, 10, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, pane, 10, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, pane, -10, SpringLayout.SOUTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, pane, -10, SpringLayout.EAST, contentPanel);
		
		contentPanel.add(pane);
		
		JLabel lblProcess = new JLabel("Process");
		pane.setColumnHeaderView(lblProcess);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
			JLabel lblForChangesTo = new JLabel("For changes to take affect a restart of the program is necessary");
			buttonPane.add(lblForChangesTo);
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
	}
}
