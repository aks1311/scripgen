package com.airbusds.idea.gui;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.airbusds.idea.model.JobResult;
import com.airbusds.idea.model.XYPlot;

@SuppressWarnings("serial")
public class ResultsPanel extends JPanel {
	
	public ResultsPanel(XYPlot plot) {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Chart", plot.createChart());
		
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
	}
	
	public ResultsPanel(JobResult result) {
		JTabbedPane tabbedPane = new JTabbedPane();
		JComponent tablePanel = result.createTable();
		tabbedPane.addTab("Chart", result.createChart());
		tabbedPane.addTab("Table", tablePanel);
		
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
	}
	
	
}
