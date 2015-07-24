package com.airbusds.idea.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
public class InputFile extends InfoBase{
	
	private String name;
	private Map<String, Parameter> parameters = new HashMap<String, Parameter>();
	private Document document = new Document();
	
	@JsonIgnore
	private boolean validated;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public List<Parameter> getParameters() {
		return new ArrayList<Parameter>( parameters.values() );
	}

	public void setParameters(List<Parameter> parameters) {
		
		this.parameters.clear();
		for (Iterator<Parameter> it = parameters.iterator(); it.hasNext();) {
			Parameter parameter = it.next();
			this.parameters.put(parameter.getName(), parameter);
		}
		
	}

	public Parameter getParameter(String paramName){
		return parameters.get(paramName);
	}
	
	public void addParameter(Parameter param){
		parameters.put(param.getName(), param);
	}
	
	public void deleteParameter(String paramName){
		parameters.remove(paramName);
	}
	
	public List<Parameter> getParametersInParameterAnalysis(){
		List<Parameter> list = new ArrayList<Parameter>(); 
		
		for (Iterator<Parameter> it = parameters.values().iterator(); it.hasNext();) {
			Parameter param = it.next();
			if(param.isPAEnabled()){
				list.add(param);
			}
		}
		
		return list;
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		if(validated == this.validated)
			return;
		
		boolean oldValue = this.validated;
		this.validated = validated;
        this.pcs.firePropertyChange("value", oldValue, validated);
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
