package com.airbusds.idea.gui.editor;

import javax.swing.JPanel;

import com.airbusds.idea.model.Parameter;

@SuppressWarnings("serial")
public abstract class PAFieldEditor extends JPanel {
	protected Parameter param;
	
	public PAFieldEditor(Parameter param){
		this.param = param;
		if(!this.param.isPAEnabled())
			throw new RuntimeException("Parameter Studies not enabled for the field");
		
	}
	
	public abstract void update();
}
