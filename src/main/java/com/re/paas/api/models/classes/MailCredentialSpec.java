package com.re.paas.api.models.classes;

public class MailCredentialSpec {

	private String providerUrl;
	private String username;
	private String password;
	
	public MailCredentialSpec() {
		
	}
	
	public MailCredentialSpec(String providerUrl, String username, String password) {
		this.providerUrl = providerUrl;
		this.username = username;
		this.password = password;
	}

	public String getProviderUrl() {
		return providerUrl;
	}

	public MailCredentialSpec setProviderUrl(String providerUrl) {
		this.providerUrl = providerUrl;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public MailCredentialSpec setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public MailCredentialSpec setPassword(String password) {
		this.password = password;
		return this;
	}

}
