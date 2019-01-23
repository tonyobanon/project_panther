package com.re.paas.internal.infra.cache.redis;

public class RedisConfig {

	private String host;
	private Integer port;
	private String database;

	private String username;
	private String password;
	private Long maxConnections;

	public String getHost() {
		return host;
	}

	public RedisConfig setHost(String host) {
		this.host = host;
		return this;
	}

	public Integer getPort() {
		return port;
	}

	public RedisConfig setPort(Integer port) {
		this.port = port;
		return this;
	}

	public String getDatabase() {
		return database;
	}

	public RedisConfig setDatabase(String database) {
		this.database = database;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public RedisConfig setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public RedisConfig setPassword(String password) {
		this.password = password;
		return this;
	}

	public Long getMaxConnections() {
		return maxConnections;
	}

	public RedisConfig setMaxConnections(Long maxConnections) {
		this.maxConnections = maxConnections;
		return this;
	}

}
