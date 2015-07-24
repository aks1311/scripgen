package com.airbusds.idea.model;

import com.airbusds.idea.gui.PAValueUtility;

public class ParamValueCombination {

	private String[] params;
	private Value[][][] values;
	private String label;
	
	public String[] getParams() {
		return params;
	}
	public void setParams(String[] params) {
		this.params = params;
	}
	public Value[][][] getValues() {
		return values;
	}
	public void setValues(Value[][][] values) {
		this.values = values;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[  ");
		for (int i = 0; i < params.length; i++) {
			sb.append("\n   "+params[i]);
				sb.append("\n      "+ PAValueUtility.getPAValueString(values[i]));
			
		}
		sb.append("]");
		return sb.toString();
	}
	
	
	
	
	@Override
	public boolean equals(Object obj) {
		
		if(!(obj instanceof ParamValueCombination))
			return false;
		
		ParamValueCombination comb = (ParamValueCombination)obj;		
		boolean result = true;
		
		if(!toString().equals(comb.toString())){
			result = false;
		}
		return result;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
}
