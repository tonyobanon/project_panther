package com.re.paas.internal.databases;

import java.util.Map;

import com.re.paas.internal.classes.ResourceFile;

public class DatabaseConfig extends ResourceFile {

	private static DatabaseConfig instance;
	
	private String adapterName;
	private Map<String, String> fields;
	
	public DatabaseConfig() {
		super("dbProfile.json");
	}
	
	public static DatabaseConfig get() {

		if (instance != null) {
			return instance;
		}
		
		instance = new DatabaseConfig().load(DatabaseConfig.class);
		return instance;
	}
	
	public String getAdapterName() {
		return adapterName;
	}

	public DatabaseConfig setAdapterName(String adapterName) {
		this.adapterName = adapterName;
		return this;
	}

	public Map<String, String> getFields() {
		return fields;
	}

	public DatabaseConfig setFields(Map<String, String> fields) {
		this.fields = fields;
		return this;
	}
}
