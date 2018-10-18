package com.re.paas.internal.cloud.azure;

import java.io.StringReader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
		
		JsonObject obj = new JsonObject();
		
		obj.addProperty("username", username);
		obj.addProperty("password", password);
		
		if(keyPair != null){
			obj.addProperty("keyPair", keyPair.toString());
		}

		return obj.toString();
	}

	@Override
	public InstanceCredential fromString(String instanceId, String stringVal) {
		
		JsonObject credentialObj = new JsonParser().parse(stringVal).getAsJsonObject();
		
		String username = credentialObj.get("username").getAsString();
		String password = credentialObj.get("password").getAsString();
		
		String keyPair = credentialObj.get("keyPair").getAsString();
		
		AzureVMCredential o = new AzureVMCredential(instanceId, username, password);
		
		if(keyPair != null) {
			o.withKeyPair(RSAKeyPair.fromString(keyPair));
		}
		
		return o;
	}
}
