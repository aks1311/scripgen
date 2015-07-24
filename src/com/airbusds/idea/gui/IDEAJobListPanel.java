package com.airbusds.idea.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class IDEAJobListPanel extends JScrollPane {

	private JPanel panel;

	IDEAJobListPanel(){
		super(new JPanel());
		this.panel = (JPanel)getViewport().getView();
		init();
	}
	

	private void init() {
		panel.add(new JLabel("TBD ...."));
	}
	
}
