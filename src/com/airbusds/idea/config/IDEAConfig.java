package com.airbusds.idea.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.airbusds.idea.utilities.StringUtils;

public class IDEAConfig {
	
	private static Logger log = LogManager.getLogger(IDEAConfig.class.getName());
	private static IDEAConfig config;
	
	Properties defaultProp = new Properties();
	Properties prop;
	
	private IDEAConfig(){}
	
	public static IDEAConfig getInstance(){
		synchronized (IDEAConfig.class) {
			if(config==null){
				config = new IDEAConfig();
			}
			try {
				config.defaultProp.load(config.getClass().getResourceAsStream("/config/default-setup.properties"));
				log.debug("Loaded default setup from /config/default-setup.properties");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return config;
	}
	
	public void setUpConfig(String ideaConfigFilePath){
		prop = readProperties(ideaConfigFilePath);
		log.debug("Loaded setup from "+ideaConfigFilePath);
	}
	
	public static List<String> getIDEACommand(){
		return config.getProperties("command");
	}

	public static int getMaxProcesses() {
		String processors = config.getProperty("max.cores");
		return StringUtils.hasText(processors)?
				Integer.parseInt(processors):
					Runtime.getRuntime().availableProcessors();
	}
	
	public String getProperty(String propName){
		if(prop!=null){
			if(prop.containsKey(propName))
				return prop.getProperty(propName);
		}else{
			if(defaultProp.containsKey(propName))
				return defaultProp.getProperty(propName);
		}
		return null;
	}
	
	public List<String> getProperties(String propName){
		List <String> list = new ArrayList<String>();
		
		int counter = 1;
		if(prop!=null){
			while(prop.containsKey(propName+"."+counter)){
				list.add(prop.getProperty(propName+"."+counter++));
			}
		}else{
			while(defaultProp.containsKey(propName+"."+counter)){
				list.add(defaultProp.getProperty(propName+"."+counter++));
			}
		}
		
		return list;
	}
	
	private Properties readProperties(String configFilePath){
		Properties prop = new Properties();
		InputStream input = null;
	 
		try {
			 
			input = new FileInputStream(configFilePath);
	 
			// load a properties file
			prop.load(input);
	 
		} catch (IOException ex) {
			log.error("Properties could be read.");
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return prop;
	}
	
	public static String getParamConfigPath(){
		return config.getProperty("config.param");
	}
	public static String getModelConfigPath(){
		return config.getProperty("model.param");
	}
	
	
}
