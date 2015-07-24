package com.airbusds.idea.gui.editor;

import java.awt.BorderLayout;

import com.airbusds.idea.model.Parameter;

@SuppressWarnings("serial")
public class ParameterAnalysisEditPanel extends EditorPanel {
	
	private Parameter _param;
	private PAFieldEditor paEditor;
	
	public ParameterAnalysisEditPanel(Parameter param){
		if(!param.isPAEnabled()){
			throw new RuntimeException("Parameter is not configured for Parameter Studies.");
		}
		_param = param;
		init();
	}
	
	public void update(){
		// delegate the paEditor for the data type
		paEditor.update();
	}
	
	private void init(){
		
		String paramType = _param.getDataType();
		if(_param.getIsTabularData()){
			paramType = "table";
		}
		
		switch(paramType){
			case "list":{
				paEditor = new ListPAEditor(_param);
			}
			break;
			case "string":{
				paEditor = new StringPAEditor(_param);
			}
			break;
			case "int":{
				paEditor = new IntPAEditor(_param);
			}
			break;
			case "float":{
				paEditor = new FloatPAEditor(_param);
			}
			break;
			case "file":{
				paEditor = new FilePAEditor(_param);
			}
			break;
			case "table":{
				paEditor = new TablePAEditor(_param);
			}
			break;
			default:
				paEditor = new StringPAEditor(_param);
		}
		this.setLayout(new BorderLayout());
		add(paEditor, BorderLayout.CENTER);
		
	}
	
	
}
