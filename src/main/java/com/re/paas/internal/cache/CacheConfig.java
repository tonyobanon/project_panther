package com.re.paas.internal.cache;

import com.re.paas.internal.classes.ResourceFile;

public class CacheConfig extends ResourceFile {

	private static CacheConfig instance;

	private String host;
	private Integer port;
	private String database;

	private String username;
	private String password;
	private Long maxConnections;

	public CacheConfig() {
		super("cacheProfile.json");
	}
	
	public static CacheConfig get() {

		if (instance != null) {
			return instance;
		}
		
		instance = new CacheConfig().load(CacheConfig.class);
		return instance;
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getDatabase() {
		return database;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public Long getMaxConnections() {
		return maxConnections;
	}

}
