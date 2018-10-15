package com.re.paas.internal.classes.gsonserializers;

import java.lang.reflect.Type;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.re.paas.internal.crypto.CryptoConstants;
import com.re.paas.internal.crypto.CryptoUtils;
import com.re.paas.internal.crypto.RSAKeyPair;

public class RSAKeyPairDeserializer implements JsonDeserializer<RSAKeyPair> {

	@Override
	public RSAKeyPair deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		
		JsonObject obj = (JsonObject) json;
		
		RSAPrivateKey privateKey = (RSAPrivateKey) CryptoUtils.toPublicKey(CryptoConstants.RSA, obj.get(CryptoConstants.PRIVATE_KEY).getAsString());
		RSAPublicKey publicKey = (RSAPublicKey) CryptoUtils.toPublicKey(CryptoConstants.RSA, obj.get(CryptoConstants.PUBLIC_KEY).getAsString());
				
		return new RSAKeyPair(privateKey, publicKey);
	}

} 
