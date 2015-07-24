package com.airbusds.idea.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.airbusds.idea.model.IDEAJob;

@SuppressWarnings("serial")
public class IDEAJobDetailsPanel extends JPanel {

	private IDEAJob job;

	IDEAJobDetailsPanel() {
	}

	public IDEAJobDetailsPanel(IDEAJob job) {
		this();
		this.job = job;
		
		this.setLayout(new BorderLayout());
		this.add(new JobSummaryPane(this.job), BorderLayout.NORTH);
		this.add(new OutputPane(this.job), BorderLayout.CENTER);
	}

}
