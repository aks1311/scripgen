package com.airbusds.idea.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class Value implements Serializable{
	
	private Object value;
	
	@JsonCreator
	private Value(@JsonProperty("value") Object value){
		if(value == null){
			System.err.println("Possible error >> setting null in Value()");
		}
		if( (value instanceof Float) 
				|| (value instanceof Integer) 
				|| (value instanceof Double) 
				|| (value instanceof Long) 
				|| (value instanceof String)){
			this.value = value;
		}else{
			throw new RuntimeException("Invalid value type being saved as Value:"+value.getClass());
		}
	}
	
	public Object getValue(){
		return value;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
	
	public static Value valueFactory(Object value){
		if(value == null){
			System.err.println("Possible error >> setting null in Value()");
			return null;
		}
		return new Value(value);
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		Value val = (Value)obj;
		return toString().equals(val.toString());
	}
	
}
