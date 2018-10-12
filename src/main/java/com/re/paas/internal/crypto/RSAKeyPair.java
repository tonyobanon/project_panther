package com.re.paas.internal.crypto;

import java.io.StringReader;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.commons.codec.binary.Base64;

import com.re.paas.api.classes.Exceptions;

public class RSAKeyPair {

	private final String privateKey;
	private final String publicKey;

	private RSAKeyPair(String privateKey, String publicKey) {
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}

	public RSAKeyPair() {

		PrivateKey priv = null;
		PublicKey pub = null;

		try {

			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			SecureRandom random = SecureRandom.getInstanceStrong();

			keyGen.initialize(1024, random);

			KeyPair pair = keyGen.generateKeyPair();

			priv = pair.getPrivate();
			pub = pair.getPublic();

		} catch (NoSuchAlgorithmException e) {
			Exceptions.throwRuntime(e);
		}
		
		 Base64 base64 = new Base64();

		 this.privateKey = new String(base64.encode(priv.getEncoded()));
		 this.publicKey = new String(base64.encode(pub.getEncoded()));
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	
	public String toString() {
		JsonObjectBuilder builder = Json.createObjectBuilder().add("privateKey", privateKey).add("publicKey",
				publicKey);
		return builder.toString();
	}
	
	public static RSAKeyPair fromString(String stringVal) {
		JsonObject credentialObj = Json.createReader(new StringReader(stringVal)).readObject();
		return new RSAKeyPair(credentialObj.getString("privateKey"), credentialObj.getString("publicKey"));
	}
}
