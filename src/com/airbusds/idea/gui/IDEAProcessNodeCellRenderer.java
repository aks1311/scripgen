package com.airbusds.idea.gui;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.airbusds.Utilities;
import com.airbusds.idea.model.IDEAJob;
import com.airbusds.idea.model.IDEAJob.Status;
import com.airbusds.idea.model.InputFile;

@SuppressWarnings("serial")
public class IDEAProcessNodeCellRenderer extends DefaultTreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		
		Component comp = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
		Object userVal = node.getUserObject();
		
		if( userVal instanceof IDEAJob ){
		
			IDEAJob job = (IDEAJob)userVal;
			ImageIcon waiting = Utilities.createImageIcon("images/waiting14.png");
	        ImageIcon running = Utilities.createImageIcon("images/running14.png");
	        ImageIcon success = Utilities.createImageIcon("images/success14.png");
	        ImageIcon failure = Utilities.createImageIcon("images/crashed14.png");
			
			if(job.getStatus().equals(Status.NOT_STARTED)){
				setIcon(waiting);
			}else if(job.getStatus().equals(Status.RUNNING)){
				setIcon(running);
			}else if(job.getStatus().equals(Status.COMPLETED)){
				setIcon(success);
			}else if(job.getStatus().equals(Status.CRASHED)){
				setIcon(failure);
			}
			
			return this;
		
		}else if(userVal instanceof InputFile){
			InputFile file = (InputFile)userVal;
			ImageIcon valid = Utilities.createImageIcon("images/document-valid14.png");
	        ImageIcon invalid = Utilities.createImageIcon("images/document-invalid14.png");
			if(file.isValidated())
				setIcon(valid);
			else
				setIcon(invalid);
			return this;
		}else if(userVal.equals("Processes")){
			ImageIcon processes = Utilities.createImageIcon("images/gears14.png");
			setIcon(processes);
			return this;
		}else if(userVal.equals("Parameter Analysis")){
			ImageIcon processes = Utilities.createImageIcon("images/chart-14.png");
			setIcon(processes);
			return this;
		}else if(userVal.equals("Results")){
			ImageIcon processes = Utilities.createImageIcon("images/results14.png");
			setIcon(processes);
			return this;
		}else{
			return comp;
		}
	}
	
}
