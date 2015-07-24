package com.airbusds.idea.manager;

import java.util.Iterator;

import com.airbusds.idea.config.InputFileConfig;
import com.airbusds.idea.config.InputFileConfig.Params;
import com.airbusds.idea.model.InputFile;
import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.model.Value;
import com.airbusds.idea.utilities.StringUtils;

public class ConfigManager {
	
	public static final String PARAM_IN = "paramIn-config.json";
	public static final String MODEL_IN = "modelIn-config.json";
	
	public void updateConfigInInputFile(InputFile inFile, String inputFile) {
		InputFileConfig config = inputFile.equals(PARAM_IN)?
				InputFileConfig.getParamInConfig():
					InputFileConfig.getModelInConfig();
		for (Iterator<Params> it = config.paramList.iterator(); it.hasNext();) {
			Params row = it.next();
			Parameter param = inFile.getParameter((String) row.name);
			if (param == null) {
				// Action if the param in config is not present in old file
				// Create and add Param and update the document
				continue;
			}
			loadParameterSettings(param, row);
		}
	}
	
	
	public Parameter loadParameterSettings(Parameter param, Params paramConfig) {
		param.setTitle((String) paramConfig.title);
		param.setName((String) paramConfig.name);
		param.setPAAllowed(paramConfig.isPAAllowed);
		
		param.setDataType(paramConfig.dataType);
		param.setShortDescription(paramConfig.shortDesc);
		param.setValidationRules(paramConfig.validationRules);
		param.setIsTabularData(paramConfig.isTabularData);
		param.setReadOnly(paramConfig.isReadOnly);
		if (StringUtils.hasText(paramConfig.defaultValue)) {
			param.setMandatory(false);
		} else {
			param.setMandatory(paramConfig.isMandatory);
		}
		param.setListName(paramConfig.listName);
		if (param.getIsTabularData()) {
			param.setColumnNames(paramConfig.columnNames);
			param.setColumnTypes(paramConfig.columnTypes);
			param.setSingleRow(paramConfig.isSingleRow);
		}
		return param;
	}

	public Parameter createParameter(Parameter param, Params paramConfig) {
		
		loadParameterSettings(param, paramConfig);
		
		param.setPAEnabled(paramConfig.isPAEnabled);
		param.setOrder(paramConfig.order);
		param.setDescription(paramConfig.description);
		
		return param;
	}
	
	public Parameter createParameter(Params paramConfig){
		Parameter param = new Parameter();
		
		// set default value only for a new parameter.
		if (StringUtils.hasText(paramConfig.defaultValue)) 
			param.setValue(Value.valueFactory(paramConfig.defaultValue));
		
		return createParameter(param, paramConfig);
	}
	
}
