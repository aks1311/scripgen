package com.airbusds.idea;

import java.io.File;

public abstract class ObjectSerializer {
	
	abstract public String getFormatName();
	
	abstract public void serializeToFile(File file, Object obj);
	abstract public File serializeToFile(String path, Object obj);
	
	abstract public <T> T getAsObject(File file, Class<T> klass);
	abstract public <T> T getAsObject(String path, Class<T> klass);
	
	abstract public String getAsString(Object obj);
	
}
