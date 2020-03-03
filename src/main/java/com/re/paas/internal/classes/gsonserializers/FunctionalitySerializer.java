package com.re.paas.internal.classes.gsonserializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.re.paas.api.fusion.functionalities.Functionality;

public class FunctionalitySerializer implements JsonSerializer<Functionality> {

	@Override
	public JsonElement serialize(Functionality src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(Functionality.toString(src));
	}

}
