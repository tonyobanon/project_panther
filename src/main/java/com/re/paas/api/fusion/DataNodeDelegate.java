package com.re.paas.api.fusion;

import com.re.paas.api.Singleton;

public interface DataNodeDelegate {

	static DataNodeDelegate get() {
		return Singleton.get(DataNodeDelegate.class);
	}
	
	Object getProperty(String sessionId, String path, String type);
	
	void setProperty(String sessionId, String path, Object value);
	
	String toPropertyString(String sessionId, String path);
}
