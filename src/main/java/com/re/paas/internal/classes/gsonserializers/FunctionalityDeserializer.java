package com.re.paas.internal.classes.gsonserializers;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.re.paas.api.fusion.services.Functionality;

public class FunctionalityDeserializer implements JsonDeserializer<Functionality> {

	@Override
	public Functionality deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		return Functionality.fromString(json.getAsString());
	}

} 
