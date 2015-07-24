package com.airbusds.idea.model.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationResults {
	
	private List<String> errors = new ArrayList<String>();
	private List<String> warnings = new ArrayList<String>();
	
	public List<String> getErrors(){
		return errors;
	}
	
	public List<String> getWarnings(){
		return warnings;
	}
	
	public void addError(String error){
		errors.add(error);
	}

	public void addWarning(String warning){
		warnings.add(warning);
	}

}
