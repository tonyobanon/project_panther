package com.re.paas.internal.classes.gsonserializers;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.re.paas.internal.utils.FrontendObjectMarshaller;

public class DateDeserializer implements JsonDeserializer<Date> {

	@Override
	public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		return FrontendObjectMarshaller.unmarshalDate(json.getAsString());
	}

} 
