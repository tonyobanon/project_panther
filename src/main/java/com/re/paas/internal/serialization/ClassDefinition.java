package com.re.paas.internal.serialization;

public class ClassDefinition {
	
	private final String appId;
	private final String className;
	
	
	public ClassDefinition(String appId, String className) {
		super();
		this.appId = appId;
		this.className = className;
	}
	
	public String getAppId() {
		return appId;
	}
	
	public String getClassName() {
		return className;
	}
	
}
