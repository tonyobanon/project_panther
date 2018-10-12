package com.re.paas.internal.classes;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.re.paas.internal.classes.gsonserializers.DateDeserializer;
import com.re.paas.internal.classes.gsonserializers.DateSerializer;
import com.re.paas.internal.classes.gsonserializers.FunctionalityDeserializer;
import com.re.paas.internal.classes.gsonserializers.FunctionalitySerializer;

public class GsonFactory {

	private static final Gson instance = createInstance();

	private static Gson createInstance() {

		return new GsonBuilder().enableComplexMapKeySerialization().setDateFormat(DateFormat.LONG)
				.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).setPrettyPrinting()

				.registerTypeAdapter(Date.class, new DateSerializer())
				.registerTypeAdapter(Date.class, new DateDeserializer())
				.registerTypeAdapter(Date.class, new FunctionalitySerializer())
				.registerTypeAdapter(Date.class, new FunctionalityDeserializer())

				.create();
	}

	public static Gson getInstance() {
		return instance;
	}

	public static <T> T fromJson(String json, Class<T> type) {
		return instance.fromJson(json, type);
	}

	public static <V> Map<String, String> toMap(String obj) {
		return toMap(parse(obj));
	}

	public static JsonObject parse(String obj) {
		return new JsonParser().parse(obj).getAsJsonObject();
	}

	public static <V> Map<String, String> toMap(JsonObject obj) {

		Map<String, String> entries = new HashMap<>(obj.size());
		obj.entrySet().forEach((e) -> {
			entries.put(e.getKey(), e.getValue().getAsString());
		});
		return entries;
	}

	static {
	}
}
