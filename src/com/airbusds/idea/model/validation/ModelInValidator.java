package com.airbusds.idea.model.validation;

import com.airbusds.idea.model.InputFile;
import com.airbusds.idea.model.Parameter;

public class ModelInValidator implements DocumentValidator {

	@Override
	public ValidationResults validate(InputFile document) {
		
		ValidationResults result = ValidationHelper.validateForMandatoryFields(document.getParameters());
		
		Parameter FCS = document.getParameter("FCS");
		if( FCS!=null && ValidationHelper.isTabularDataPresent(FCS)){
			Parameter FCS_state_space_A = document.getParameter("FCS_state_space_A");
			if( !ValidationHelper.isTabularDataPresent(FCS_state_space_A) )
				result.addError("FCS_state_space_A is required.");
			
			Parameter FCS_state_space_B = document.getParameter("FCS_state_space_B");
			if( !ValidationHelper.isTabularDataPresent(FCS_state_space_B) )
				result.addError("FCS_state_space_B is required.");
			
			Parameter FCS_state_space_C = document.getParameter("FCS_state_space_C");
			if( !ValidationHelper.isTabularDataPresent(FCS_state_space_C) )
				result.addError("FCS_state_space_C is required.");
			
			Parameter FCS_state_space_D = document.getParameter("FCS_state_space_D");
			if( !ValidationHelper.isTabularDataPresent(FCS_state_space_D) )
				result.addError("FCS_state_space_D is required.");
			
			Parameter FCS_modal2sensors = document.getParameter("FCS_modal2sensors");
			if( !ValidationHelper.isTabularDataPresent(FCS_modal2sensors) )
				result.addError("FCS_modal2sensors is required.");
			
			Parameter FCS_actuators2modal = document.getParameter("FCS_actuators2modal");
			if( !ValidationHelper.isTabularDataPresent(FCS_actuators2modal) )
				result.addError("FCS_actuators2modal is required.");

		}
		
		Parameter GAF = document.getParameter("GAF");
		Parameter GAF_RAE = document.getParameter("GAF_RAE");
		Parameter GAF_method = document.getParameter("GAF_method");
		
		if(GAF!=null && GAF_RAE!=null){
			if(!ValidationHelper.isTabularDataPresent(GAF) && !ValidationHelper.isTabularDataPresent(GAF_RAE)){
				result.addError("Atlease one of GAF and GAF_RAE need to be entered. ");
			}else if(ValidationHelper.isTabularDataPresent(GAF) && ValidationHelper.isTabularDataPresent(GAF_RAE)){
				result.addError("Only one of GAF and GAF_RAE need to be entered. ");
			}
		}
		
		if(GAF_method!=null && GAF_RAE!=null){
			if(GAF_method.getValue()!=null && ValidationHelper.isTabularDataPresent(GAF_RAE)){
				result.addWarning("GAF_method will be ignored as GAF_RAE is present");
			}
		}
		
		return result;
	}

}
