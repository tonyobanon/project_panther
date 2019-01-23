package com.re.paas.internal.classes.gsonserializers;

import java.lang.reflect.Type;
import java.security.interfaces.RSAPrivateKey;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.re.paas.internal.crypto.impl.CryptoConstants;
import com.re.paas.internal.crypto.impl.CryptoUtils;

public class RSAPrivateKeySerializer implements JsonSerializer<RSAPrivateKey> {

	@Override
	public JsonElement serialize(RSAPrivateKey src, Type typeOfSrc, JsonSerializationContext context) {
		String privateKey = CryptoUtils.asString(CryptoConstants.RSA, src);
		return new JsonPrimitive(privateKey);
	}
}
