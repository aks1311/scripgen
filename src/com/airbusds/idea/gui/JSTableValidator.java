package com.airbusds.idea.gui;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.airbusds.idea.utilities.StringUtils;

public class JSTableValidator extends BaseJSValidator implements TableValidator {

	String rules;

	public JSTableValidator(String validationRules) {
		rules = validationRules;
	}

	@Override
	public ValidationResult validateTableData(Object[][] data, String[] colNames) {
		ValidationResult result = new ValidationResult();
		result.isError = false;
		
		ScriptEngine js = new ScriptEngineManager()
				.getEngineByName("javascript");
		Bindings bindings = js.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("columnNames", colNames);
		
		String[] splitRules = rules.split(";");
		for (int i = 0; i < splitRules.length; i++) {
			String rule = splitRules[i];
			String message = rule.substring(rule.indexOf("#")+1);
			rule = rule.substring(0, rule.indexOf('#'));
			
			rule = fixRuleString(rule, colNames.length);
			for (int j = 0; j < data.length; j++) {
				Object[] row = data[j];
				
				bindings.put("row", row);
				try {
					js.eval(rule);
					boolean valid = (Boolean)js.get("valid");
					if(!valid){
						result.isError = true;
						
						if(StringUtils.hasText(message))
							result.message = fixMessageString(message, colNames, row.length);
						else
							result.message = "Invalid data entered.";
						result.errRow = j;
					}
						
				} catch (ScriptException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
		
		return result;
	}
	
	private String fixRuleString(String rule, int colCount) {
		while(--colCount>=0){
			rule = rule.replace("{"+(colCount+1)+"}", "row["+colCount+"]");
		}
		rule = "if(!("+rule+")) valid = false; else valid = true;";
		return rule;
	}

	private String fixMessageString(String message, String[] colNames, int colCount) {
		while(--colCount>=0){
			message = message.replace("{"+(colCount+1)+"}", colNames[colCount]);
		}
		return message;
	}

	
}
