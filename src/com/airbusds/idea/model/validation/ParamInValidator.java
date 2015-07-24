package com.airbusds.idea.model.validation;

import com.airbusds.Utilities;
import com.airbusds.idea.model.InputFile;
import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.model.Value;

public class ParamInValidator implements DocumentValidator {

	@Override
	public ValidationResults validate(InputFile document) {
		
		ValidationResults result = ValidationHelper.validateForMandatoryFields(document.getParameters());
		
		Parameter flutter_density = document.getParameter("flutter_density");
		Parameter flutter_V_sound = document.getParameter("flutter_V_sound");
		Parameter flutter_altitude = document.getParameter("flutter_altitude");
		
		if(!flutter_density.isPAEnabled() && !flutter_altitude.isPAEnabled() && !flutter_V_sound.isPAEnabled() ){
		
			String flutter_density_value = null;
			if( !ValidationHelper.isTabularDataPresent(flutter_density)){
				result.addError(Utilities.getParamDisplayName(flutter_density)+" is mandatory");
			}else{
				Value[][] data = flutter_density.getTabularData();
				flutter_density_value = data[0][0].getValue().toString();
			}
			
			String flutter_V_sound_value = null;
			if( !ValidationHelper.isTabularDataPresent(flutter_V_sound)){
				result.addError(Utilities.getParamDisplayName(flutter_V_sound)+" is mandatory");
			}else{
				Value[][] data = flutter_V_sound.getTabularData();
				flutter_V_sound_value = data[0][0].getValue().toString();
			}
			
			if(("ISA".equals(flutter_V_sound_value) || "ISA".equals(flutter_density_value)) && !ValidationHelper.isTabularDataPresent(flutter_altitude)){
				result.addError(Utilities.getParamDisplayName(flutter_altitude)+" is required.");
			}
			if( !"ISA".equals(flutter_V_sound_value) && !"ISA".equals(flutter_density_value) && ValidationHelper.isTabularDataPresent(flutter_altitude)){
				result.addWarning("As niether flutter_density nor flutter_V_sound is not set to ISA "+Utilities.getParamDisplayName(flutter_altitude)+" will be ignored.");
			}
			
		
		}
		
		return result;
	}

}
