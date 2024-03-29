package com.re.paas.internal.infra.cache.redis;

public class RedisConfig {

	private String host;
	private Integer port;
	private Integer database;

	private String username;
	private String password;

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

	public Integer getDatabase() {
		return database;
	}

	public RedisConfig setDatabase(Integer database) {
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

}
