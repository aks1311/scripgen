package com.airbusds.idea.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.model.Range;
import com.airbusds.idea.model.Value;

public class PAValueUtility {


	public static String getPAValueString(Value[][] values) {
		return getPAValueString(values, " : ");
	}
	
	public static String getPAValueString(Value[][] values, String delim) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < values.length; i++) {
			if(values[i]!=null){
				
				if (i != 0)
					sb.append("\n");
				
				for (int j = 0; j < values[i].length; j++) {
					if(j!=0)
						sb.append(delim);
					sb.append(values[i][j].getValue());	
					
				}
			}
				
		}

		return sb.toString();
	}
	
	public static String getPAValueHTML(Value[][] values) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		for (int i = 0; i < values.length; i++) {
			if(values[i]!=null){
				
				if (i != 0)
					sb.append("<br />");
				
				for (int j = 0; j < values[i].length; j++) {
					if(j!=0)
						sb.append(" : ");
					sb.append(values[i][j].getValue());	
					
				}
			}
				
		}

		return sb.toString();
	}
	
	public static Value[] createValuesForRanges(Parameter param, PAValueStatus status ){
		List<Value> result = new ArrayList<Value>();
		String dataType = param.getDataType();
		
		List<Range> ranges = param.getParameterAnalysis().getPaValueRange();
		for (Iterator<Range> itr = ranges.iterator(); itr.hasNext();) {
			Range range = itr.next();
			
			NumberFormatter formatter = new NumberFormatter();
			if(dataType.equals("int")){
				int min,steps,max;
				min = formatter.formatToInt((String)range.getMinValue().getValue());
				steps = formatter.formatToInt((String)range.getSteps().getValue());
				max = formatter.formatToInt((String)range.getMaxValue().getValue());
				
				int counter = min;
				while(counter <= max){
					Value val = Value.valueFactory(counter+"");
					if(result.contains(val)){
						status.isError = true;
						status.message += (val +", ");
					}else{
						result.add(val);
					}
					counter = counter + steps;
				}
				
			}else{
				float min,steps,max;
				min = formatter.formatToFloat((String)range.getMinValue().getValue());
				steps = formatter.formatToFloat((String)range.getSteps().getValue());
				max = formatter.formatToFloat((String)range.getMaxValue().getValue());
				
				float counter = min;
				while(counter <= max){
					Value val = Value.valueFactory(counter+"");
					if(result.contains(val)){
						status.isError = true;
						status.message += (val +", ");
					}else{
						result.add(val);
					}
					counter = counter + steps;
				}
//				if(!result.contains(max+""))
//					result.add(new Value(max+""));

			}
			
		}
		
		return result.toArray(new Value[result.size()]);
	}
	
	public static class PAValueStatus{
		boolean isError;
		String message = "";
	}
	
}

