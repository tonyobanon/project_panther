package com.re.paas.api.crytography;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.internal.crytography.impl.CryptoConstants;

public class RSAKeyPair {

	private RSAPrivateKey privateKey;
	private RSAPublicKey publicKey;

	public RSAKeyPair(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}
	
	public RSAKeyPair() {

		try {

			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(CryptoConstants.RSA);
			SecureRandom random = SecureRandom.getInstanceStrong();

			keyGen.initialize(1024, random);

			KeyPair pair = keyGen.generateKeyPair();

			this.privateKey = (RSAPrivateKey) pair.getPrivate();
			this.publicKey = (RSAPublicKey) pair.getPublic();			
			
		} catch (NoSuchAlgorithmException e) {
			Exceptions.throwRuntime(e);
		}
		
	}

	public RSAPrivateKey getPrivateKey() {
		return privateKey;
	}

	public RSAPublicKey getPublicKey() {
		return publicKey;
	}

}
