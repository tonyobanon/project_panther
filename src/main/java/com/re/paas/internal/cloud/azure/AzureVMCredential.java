package com.re.paas.internal.cloud.azure;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import com.google.gson.JsonObject;
import com.re.paas.api.cryto.RSAKeyPair;
import com.re.paas.api.infra.cloud.CloudEnvironment;
import com.re.paas.api.infra.cloud.InstanceCredential;
import com.re.paas.internal.classes.Json;
import com.re.paas.internal.crypto.impl.CryptoUtils;

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
			obj.addProperty("rsaPublicKey", CryptoUtils.asString("RSA", keyPair.getPublicKey()));
			obj.addProperty("rsaPrivateKey", CryptoUtils.asString("RSA", keyPair.getPrivateKey()));
		}

		return obj.toString();
	}

	@Override
	public InstanceCredential fromString(String instanceId, String stringVal) {
		
		JsonObject credentialObj = Json.parse(stringVal);
		
		String username = credentialObj.get("username").getAsString();
		String password = credentialObj.get("password").getAsString();
		
		String rsaPublicKey = credentialObj.get("rsaPublicKey").getAsString();
		String rsaPrivateKey = credentialObj.get("rsaPrivateKey").getAsString();
	
		AzureVMCredential o = new AzureVMCredential(instanceId, username, password);
		
		if(keyPair != null) {
			
			RSAPrivateKey privateKey = (RSAPrivateKey) CryptoUtils.toPrivateKey("RSA", rsaPrivateKey);
			RSAPublicKey publicKey = (RSAPublicKey) CryptoUtils.toPrivateKey("RSA", rsaPublicKey);
			
			o.withKeyPair(new RSAKeyPair(privateKey, publicKey));
		}
		
		return o;
	}
}
