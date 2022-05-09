package com.re.paas.internal.classes.gsonserializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.re.paas.api.crytography.RSAKeyPair;
import com.re.paas.internal.crytography.impl.CryptoConstants;
import com.re.paas.internal.crytography.impl.CryptoUtils;

public class RSAKeyPairSerializer implements JsonSerializer<RSAKeyPair> {

	@Override
	public JsonElement serialize(RSAKeyPair src, Type typeOfSrc, JsonSerializationContext context) {
		
		String privateKey = CryptoUtils.asString(CryptoConstants.RSA, src.getPrivateKey());
		String publicKey = CryptoUtils.asString(CryptoConstants.RSA, src.getPublicKey());

		JsonObject obj = new JsonObject();
		
		obj.add(CryptoConstants.PRIVATE_KEY, new JsonPrimitive(privateKey));
		obj.add(CryptoConstants.PUBLIC_KEY, new JsonPrimitive(publicKey));
		
		return obj;
	}

}
