package com.re.paas.internal.filesystems;

import java.util.Map;

import com.re.paas.internal.classes.ResourceFile;

public class FileSystemConfig extends ResourceFile {

	private static FileSystemConfig instance;

	private String adapterName;
	private Map<String, String> fields;
	
	public FileSystemConfig() {
		super("fsProfile.json");
	}
	
	public static FileSystemConfig get() {

		if (instance != null) {
			return instance;
		}
		
		instance = new FileSystemConfig().load(FileSystemConfig.class);
		return instance;
	}

	public String getAdapterName() {
		return adapterName;
	}

	public FileSystemConfig setAdapterName(String adapterName) {
		this.adapterName = adapterName;
		return this;
	}

	public Map<String, String> getFields() {
		return fields;
	}

	public FileSystemConfig setFields(Map<String, String> fields) {
		this.fields = fields;
		return this;
	}

}
