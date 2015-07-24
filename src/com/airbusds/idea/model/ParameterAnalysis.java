package com.airbusds.idea.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean for ParameterAnalysis
 * 
 * @author amit.singh
 *
 */
public class ParameterAnalysis {
	
	private List<Value> paValueList = new ArrayList<Value>();
	private List<Range> paValueRange = new ArrayList<Range>();
	private Value[][] paRowList; // PA for single row table data
	private List<Value[][]> paTableList = new ArrayList<Value[][]>(); // PA for multiple row table data
	
	/**
	 * @return List of values defined for the Parameter Analysis
	 */
	public List<Value> getPaValueList() {
		return paValueList;
	}
	public void setPaValueList(List<Value> paValueList) {
		this.paValueList = paValueList;
	}
	
	/**
	 * @return <code>Range</code> of values for the Parameter Analysis
	 */
	public List<Range> getPaValueRange() {
		return paValueRange;
	}
	public void setPaValueRange(List<Range> paValueRange) {
		this.paValueRange = paValueRange;
	}
	
	public Value[][] getPaRowList() {
		return paRowList;
	}
	public void setPaRowList(Value[][] paRowList) {
		this.paRowList = paRowList;
	}
	public List<Value[][]> getPaTableList() {
		return paTableList;
	}
	public void setPaTableList(List<Value[][]> paTableList) {
		this.paTableList = paTableList;
	}
	
}
