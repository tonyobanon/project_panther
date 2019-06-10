package com.re.paas.internal.cloud.azure;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import com.re.paas.api.cryto.RSAKeyPair;
import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.infra.cloud.CloudEnvironment;
import com.re.paas.api.infra.cloud.InstanceCredential;
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
		
		obj.put("username", username);
		obj.put("password", password);
		
		if(keyPair != null){
			obj.put("rsaPublicKey", CryptoUtils.asString("RSA", keyPair.getPublicKey()));
			obj.put("rsaPrivateKey", CryptoUtils.asString("RSA", keyPair.getPrivateKey()));
		}

		return obj.toString();
	}

	@Override
	public InstanceCredential fromString(String instanceId, String stringVal) {
		
		JsonObject credentialObj = new JsonObject(stringVal);
		
		String username = credentialObj.getString("username");
		String password = credentialObj.getString("password");
		
		String rsaPublicKey = credentialObj.getString("rsaPublicKey");
		String rsaPrivateKey = credentialObj.getString("rsaPrivateKey");
	
		AzureVMCredential o = new AzureVMCredential(instanceId, username, password);
		
		if(keyPair != null) {
			
			RSAPrivateKey privateKey = (RSAPrivateKey) CryptoUtils.toPrivateKey("RSA", rsaPrivateKey);
			RSAPublicKey publicKey = (RSAPublicKey) CryptoUtils.toPrivateKey("RSA", rsaPublicKey);
			
			o.withKeyPair(new RSAKeyPair(privateKey, publicKey));
		}
		
		return o;
	}
}
