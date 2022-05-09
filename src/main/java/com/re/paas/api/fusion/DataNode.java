package com.re.paas.api.fusion;

import java.util.List;

public interface DataNode {

	String getAssetId();
	
	String getPath();
	
	String getSessionId();
	
	String getClassName(String type);
	
	
	default String getPath0(int i) {
		assert this instanceof List;
		
		return getPath() + "[" + i + "]";
	}
	
	default String getPath0(String p) {
		
		if (getPath() == null) {
			assert this instanceof BaseComponent;
			return p;
		}
		
		return getPath() + "." + p;
	}
	
	default Object getProperty(String p, String type) {
		return DataNodeDelegate.get().getProperty(getSessionId(), getPath0(p), type);
	}
	
	default void setProperty(String p, Object value) {
		DataNodeDelegate.get().setProperty(getSessionId(), getPath0(p), value);
	}
    
	default String toPropertyString(String p) {
		return DataNodeDelegate.get().toPropertyString(getSessionId(), getPath0(p));
	}
}
