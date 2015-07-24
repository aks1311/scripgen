package com.airbusds.idea;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONSerializer extends ObjectSerializer{

	@Override
	public void serializeToFile(File file, Object obj) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writerWithDefaultPrettyPrinter().writeValue(file, obj);
		} catch (IOException  e) {
			e.printStackTrace();
		}
	}

	@Override
	public File serializeToFile(String path, Object obj) {
		File newFile = new File(path);
		serializeToFile(newFile, obj);
		return newFile;
	}

	@Override
	public String getAsString(Object obj) {
		String retVal = "ERROR_OCCURED";
		ObjectMapper mapper = new ObjectMapper();
		try {
			retVal = mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} 
		return retVal;
	}

	@Override
	public String getFormatName() {
		return "JSON";
	}

	@Override
	public <T>T getAsObject(File file, Class<T> klass) {
		ObjectMapper mapper = new ObjectMapper();
		T t = null;
		try {
			t = mapper.readValue(file, klass);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return t;
	}

	@Override
	public <T>T getAsObject(String path, Class<T> klass) {
		
		ObjectMapper mapper = new ObjectMapper();
		T t = null;
		try {
			t = mapper.readValue(getClass().getResourceAsStream(path), klass);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return t;
	}
	
	

}
