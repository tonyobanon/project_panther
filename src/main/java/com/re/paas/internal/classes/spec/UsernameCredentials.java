package com.re.paas.internal.classes.spec;

public class UsernameCredentials {
	
	private String url;
	
	private String username;
	private String password;

	public String getUrl() {
		return url;
	}

	public UsernameCredentials setUrl(String url) {
		this.url = url;
		return this;
	}
	
	public String getUsername() {
		return username;
	}

	public UsernameCredentials setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public UsernameCredentials setPassword(String password) {
		this.password = password;
		return this;
	}

}
 