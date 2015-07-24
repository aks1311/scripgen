package com.airbusds.idea.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;


@SuppressWarnings("serial")
public class Parameter implements Serializable {
	String name;
	String title;
	String dataType;
	int order;
	String description;
	String shortDescription;
	boolean isMandatory;
	
	Value value;
	boolean isReadOnly;
	String listName;
	
	boolean isTabularData;
	String[] columnNames;
	String[] columnTypes;
	Value[][] tabularData;
	boolean isSingleRow;
	@JsonIgnore private String validationRules;
	
	private boolean isPAAllowed;
	private boolean isPAEnabled;
	private ParameterAnalysis parameterAnalysis;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getShortDescription() {
		return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	public Value getValue() {
		return value;
	}
	public void setValue(Value value) {
		this.value = value;
	}
	public boolean isReadOnly() {
		return isReadOnly;
	}
	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}
	public String getListName() {
		return listName;
	}
	public void setListName(String listName) {
		this.listName = listName;
	}
	public boolean isMandatory() {
		return isMandatory;
	}
	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}
	@JsonGetter("isTabularData")
	public boolean getIsTabularData() {
		return isTabularData;
	}
	@JsonSetter("isTabularData")
	public void setIsTabularData(boolean isTabularData) {
		this.isTabularData = isTabularData;
	}
	public String[] getColumnNames() {
		return columnNames;
	}
	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}
	public String[] getColumnTypes() {
		return columnTypes;
	}
	public void setColumnTypes(String[] columnTypes) {
		this.columnTypes = columnTypes;
	}
	
	@JsonGetter("tabularData")
	public Value[][] getTabularData() {
		return tabularData;
	}
	@JsonSetter("tabularData")
	public void setTabularData(Value[][] tabularData) {
		this.tabularData = tabularData;
	}
	public boolean isSingleRow() {
		return isSingleRow;
	}
	public void setSingleRow(boolean isSingleRow) {
		this.isSingleRow = isSingleRow;
	}
	public boolean isPAAllowed() {
		return isPAAllowed;
	}
	public void setPAAllowed(boolean isPAAllowed) {
		this.isPAAllowed = isPAAllowed;
	}
	public boolean isPAEnabled() {
		return isPAEnabled;
	}
	public void setPAEnabled(boolean isPAEnabled) {
		this.isPAEnabled = isPAEnabled;
	}
	public ParameterAnalysis getParameterAnalysis() {
		if(parameterAnalysis==null)
			parameterAnalysis = new ParameterAnalysis();
		return parameterAnalysis;
	}
	public void setParameterAnalysis(ParameterAnalysis parameterAnalysis) {
		this.parameterAnalysis = parameterAnalysis;
	}
	@JsonIgnore public String getValidationRules() {
		return validationRules;
	}
	@JsonIgnore public void setValidationRules(String validationRules) {
		this.validationRules = validationRules;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		return name.equals(((Parameter)obj).name);
	}
	
}
