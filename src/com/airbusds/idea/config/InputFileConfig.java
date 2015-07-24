package com.airbusds.idea.config;

import java.util.Iterator;
import java.util.List;

import com.airbusds.idea.JSONSerializer;
import com.airbusds.idea.utilities.StringUtils;

public class InputFileConfig {
	
	public List<Params> paramList;
	public List<IdValueList> lists;
	
	public static class Params{
		public String name; // name that goes in the input file
		public String title; // more meaning title for the name, possibly for the messages and logs
		public int order;
		public String dataType;
		public String defaultValue;
		public boolean isReadOnly;
		public String listName;
		public boolean defaultSelected;
		public String description;
		public String shortDesc;
		public boolean isPAEnabled;
		public boolean isPAAllowed = true;
		public boolean isMandatory = true;
		
		public boolean isTabularData;
		public String[] columnNames;
		public String[] columnTypes;
		public boolean isSingleRow;
		public String validationRules;
	}
	
	public static class IdValueList{
		public String name;
		public List<IdValue> list;
		@Override
		public String toString() {
			return "{name:"+name+", list["+list+"]}";
		}
	}
	
	public static class IdValue{
		public String id;
		public String value;
		
		public IdValue(){}
		public IdValue(String id, String value){
			this.id = id;
			this.value = value;
		}
		
		@Override
		public String toString() {
			return id;
		}
		
		@Override
		public boolean equals(Object obj) {
			IdValue val = (IdValue)obj;
			if(val == null)
				return false;
			return id.equals(val.id);
		}
	}
	
	private static InputFileConfig readConfig(String path){
		JSONSerializer ser = new JSONSerializer();
		return ser.getAsObject(path, InputFileConfig.class);
	}
	
	public static InputFileConfig getParamInConfig(){
		String path = IDEAConfig.getParamConfigPath();
		if(!StringUtils.hasText(path))
			path = "/config/paramIn-config.json";
		InputFileConfig config = InputFileConfig.readConfig(path);
		return config;
	}
	
	public static InputFileConfig getModelInConfig(){
		String path = IDEAConfig.getModelConfigPath();
		if(!StringUtils.hasText(path))
			path = "/config/modelIn-config.json";
		InputFileConfig config = InputFileConfig.readConfig(path);
		return config;
	}

	public static List<IdValue> getList(String listName){ // TODO 1 Lists for input files have to be separated.
		InputFileConfig config = InputFileConfig.getParamInConfig();
		for (Iterator<IdValueList> it = config.lists.iterator(); it.hasNext();) {
			IdValueList list = it.next();
			if(list.name.equals(listName))
				return list.list;
		}
		return null;
	}
	
}
