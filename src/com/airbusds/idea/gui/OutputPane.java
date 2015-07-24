package com.airbusds.idea.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.airbusds.Utilities;
import com.airbusds.idea.job.LogFileReader;
import com.airbusds.idea.model.IDEAJob;
import com.airbusds.idea.model.IDEAJob.Status;

@SuppressWarnings("serial")
public class OutputPane extends JScrollPane {
	
	IDEAJob job;
	JTextArea output;
	
	public OutputPane(final IDEAJob job) {
		super(new JTextArea());
		
		JLabel lbl = new JLabel("  Output");
		lbl.setFont(new Font(lbl.getFont().getName(), Font.BOLD, lbl.getFont().getSize()));
        setColumnHeaderView(lbl);
		
		output = (JTextArea)getViewport().getView();
		output.setEditable(false);
		
		this.job = job;
		this.setBackground(Color.WHITE);
		this.setOpaque(true);
		
		if(job.getStatus().equals(Status.COMPLETED) || job.getStatus().equals(Status.CRASHED) || job.getStatus().equals(Status.RUNNING))
			watchOutput();
		
		job.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Status stat = (Status)evt.getNewValue();
				if(stat.equals(Status.RUNNING))
					watchOutput();
			}
		});
		
	}
	
	private void watchOutput(){
		Path path = Paths.get(job.getScreenLogFile());
		output.setText("");
		LogFileReader reader = new LogFileReader(path.toFile()) {
			
			@Override
			public void onNewLine(String line) {
				output.append(line+"\n");
				Utilities.scrollToBottom(output);
			}
			
			@Override
			public boolean stopReading() {
				if (!isDisplayable() || job.getStatus().equals(Status.COMPLETED) || job.getStatus().equals(Status.CRASHED)) {
					return true;
				}else{
					return false;
				}
			}
			
		};
		reader.start();
	}
	
	public static void scrollToBottom(JComponent component) {
        Rectangle visibleRect = component.getVisibleRect();
        visibleRect.y = component.getHeight() - visibleRect.height;
        component.scrollRectToVisible(visibleRect);
    }
	
}
