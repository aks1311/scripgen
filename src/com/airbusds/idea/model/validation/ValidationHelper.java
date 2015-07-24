package com.airbusds.idea.model.validation;

import java.util.Iterator;
import java.util.List;

import com.airbusds.Utilities;
import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.model.Range;
import com.airbusds.idea.model.Value;

public class ValidationHelper {

	public static boolean isTabularDataPresent(Parameter param){
		
		if(param==null)
			return false;
		Value[][] data = param.getTabularData();
		if(data!=null && data.length>0){
			Value[] row = data[0];
			int rowSize = row.length;
			while(rowSize>0){
				if(row[--rowSize]!=null)
					return true;
			}
		}else{
			return false;
		}
		
		return false;
		
	}
	
	public static ValidationResults validateForMandatoryFields(List<Parameter> params){
		ValidationResults results = new ValidationResults();
		
		for (Iterator<Parameter> it = params.iterator(); it.hasNext();) {
			Parameter param = it.next();
			if(param.isMandatory() || param.isPAEnabled()){
				
				if(param.isPAEnabled()){
					if(param.getIsTabularData()){
						if(param.isSingleRow()){
							Value[][] data = param.getParameterAnalysis().getPaRowList();
							if(data==null || data.length==0)
								results.addError( Utilities.getParamDisplayName(param) + " is mandatory.");
						}else{
							List<Value[][]> data = param.getParameterAnalysis().getPaTableList();
							if(data==null || data.isEmpty())
								results.addError( Utilities.getParamDisplayName(param) + " is mandatory.");
						}
					}else if(param.getDataType().equals("int") || param.getDataType().equals("float")){
						List<Range> data = param.getParameterAnalysis().getPaValueRange();
						if(data==null || data.isEmpty())
							results.addError( Utilities.getParamDisplayName(param) + " is mandatory.");
					}else if(param.getDataType().equals("string")){
						List<Value> data = param.getParameterAnalysis().getPaValueList();
						if(data==null || data.isEmpty())
							results.addError( Utilities.getParamDisplayName(param) + " is mandatory.");
					}
					
				}else{
					if(param.getIsTabularData()){
						Value[][] data = param.getTabularData();
						if(data==null || data.length==0)
							results.addError( Utilities.getParamDisplayName(param) + " is mandatory.");
					}else{
						Value val = param.getValue();
						if(val==null || val.getValue()==null)
							results.addError( Utilities.getParamDisplayName(param) + " is mandatory.");
					}
				}
			}
		}
		
		return results;
	}
	
}
