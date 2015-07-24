package com.airbusds.idea;

import java.util.HashMap;
import java.util.Map;

import com.airbusds.idea.model.AnalysisInfo;

public class IDEAContext {
	
	private static IDEAContext instance;
	static{
		synchronized (IDEAContext.class) {
			instance = new IDEAContext();
		}
	}
	
	public static IDEAContext getInstance(){
		return instance;
	}
	
	private IDEAContext(){}
	
	private Map<String, Object> map = new HashMap<String, Object>();
	
	public Object get(String key){
		return map.get(key);
	}
	public void set(String key, Object obj){
		map.put(key, obj);
	}
	
	public AnalysisInfo getAnalysisInfo(){
		return (AnalysisInfo)map.get(KEY_ANALYSIS_INFO);
	}
	
	public void setAnalysisInfo(AnalysisInfo info){
		map.put(KEY_ANALYSIS_INFO, info);
	}
	
	
	public static final String KEY_ANALYSIS_INFO = "asys.info";
	public static final String KEY_WINDOW_MAIN = "window.main";
	public static final String KEY_ASYS_EDITOR = "asys.editor";
	
}
