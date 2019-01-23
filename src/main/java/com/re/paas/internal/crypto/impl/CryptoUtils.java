package com.re.paas.internal.crypto.impl;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;

import com.re.paas.api.classes.Exceptions;

public class CryptoUtils {

	public static PrivateKey toPrivateKey(String alg, String key64) {
		try {
			byte[] clear = Base64.decodeBase64(key64);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
			KeyFactory fact = KeyFactory.getInstance(alg);
			PrivateKey priv = fact.generatePrivate(keySpec);
			Arrays.fill(clear, (byte) 0);
			return priv;
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	public static PublicKey toPublicKey(String alg, String stored) {
		try {
			byte[] data = Base64.decodeBase64(stored);
			X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
			KeyFactory fact = KeyFactory.getInstance(alg);
			return fact.generatePublic(spec);
		} catch (GeneralSecurityException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	public static String asString(String alg, PrivateKey priv) {
		try {
			KeyFactory fact = KeyFactory.getInstance(alg);
			PKCS8EncodedKeySpec spec = fact.getKeySpec(priv, PKCS8EncodedKeySpec.class);
			byte[] packed = spec.getEncoded();
			String key64 = Base64.encodeBase64String(packed);

			Arrays.fill(packed, (byte) 0);
			return key64;
		} catch (GeneralSecurityException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	public static String asString(String alg, PublicKey publ) {
		try {
			KeyFactory fact = KeyFactory.getInstance(alg);
			X509EncodedKeySpec spec = fact.getKeySpec(publ, X509EncodedKeySpec.class);
			return Base64.encodeBase64String(spec.getEncoded());
		} catch (GeneralSecurityException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

}
