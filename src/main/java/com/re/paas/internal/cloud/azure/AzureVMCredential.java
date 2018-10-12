package com.re.paas.internal.cloud.azure;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.re.paas.api.cloud.CloudEnvironment;
import com.re.paas.api.cloud.InstanceCredential;
import com.re.paas.internal.crypto.RSAKeyPair;

public class AzureVMCredential extends InstanceCredential {

	private String username;
	private String password;

	private RSAKeyPair keyPair;

	@Override
	public Class<? extends CloudEnvironment> provider() {
		return AzureEnvironment.class;
	}
	
	public AzureVMCredential(String instanceId, String username, String password) {
		super(instanceId);
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public RSAKeyPair getKeyPair() {
		return keyPair;
	}

	public AzureVMCredential withKeyPair(RSAKeyPair keyPair) {
		this.keyPair = keyPair;
		return this;
	}

	@Override
	public final String toString() {
		
		JsonObjectBuilder builder = Json.createObjectBuilder()
				.add("username", username)
				.add("password", password);
		
		if(keyPair != null){
			builder.add("keyPair", keyPair.toString());
		}

		JsonObject o = builder.build();
		return o.toString();
	}

	@Override
	public InstanceCredential fromString(String instanceId, String stringVal) {
		
		JsonObject credentialObj = Json.createReader(new StringReader(stringVal)).readObject();
		
		String username = credentialObj.getString("username");
		String password = credentialObj.getString("password");
		
		String keyPair = credentialObj.getString("keyPair");
		
		AzureVMCredential o = new AzureVMCredential(instanceId, username, password);
		
		if(keyPair != null) {
			o.withKeyPair(RSAKeyPair.fromString(keyPair));
		}
		
		return o;
	}
}
