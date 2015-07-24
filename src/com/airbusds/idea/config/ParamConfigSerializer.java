package com.airbusds.idea.config;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParamConfigSerializer{


	public void serializeToFile(File file, Object obj) {
		// TODO 3 Auto-generated method stub

	}

	public File serializeToFile(String path, Object obj) {
		// TODO 3 Auto-generated method stub
		return null;
	}

	public InputFileConfig getAsObject(File file) {
		ObjectMapper mapper = new ObjectMapper();
		
		InputFileConfig t = new InputFileConfig();
		try {
			JsonNode node = mapper.readTree(file);
			node = node.get("paramList");
			TypeReference<List<InputFileConfig.Params>> typeRef = new TypeReference<List<InputFileConfig.Params>>() {};
			
			List<InputFileConfig.Params> params = mapper.readValue(node.traverse(), typeRef);
			t.paramList = params;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return t;
	}

	public InputFileConfig getAsObject(String path) {
		File newFile = new File(path);
		return getAsObject(newFile);
	}

	public String getAsString(Object obj) {
		// TODO 3 Auto-generated method stub
		return null;
	}

}
