package com.re.paas.internal.classes.gsonserializers;

import java.lang.reflect.Type;
import java.security.interfaces.RSAPrivateKey;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.re.paas.internal.crypto.CryptoConstants;
import com.re.paas.internal.crypto.CryptoUtils;

public class RSAPrivateKeyDeserializer implements JsonDeserializer<RSAPrivateKey> {

	@Override
	public RSAPrivateKey deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		return (RSAPrivateKey) CryptoUtils.toPrivateKey(CryptoConstants.RSA, json.getAsString());
	}

} 
