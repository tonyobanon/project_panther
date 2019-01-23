package com.re.paas.internal.classes;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializer;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.spi.ClassIdentityType;
import com.re.paas.api.utils.ClassUtils;

public class Json {

	private static final Logger LOG = LoggerFactory.get().getLog(Json.class);
	private static final Gson instance = createInstance();

	private static Gson createInstance() {

		GsonBuilder builder = new GsonBuilder().enableComplexMapKeySerialization().setDateFormat(DateFormat.LONG)
				.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).setPrettyPrinting();

		registerTypeAdapters(builder);

		return builder.create();
	}

	private static void registerTypeAdapters(GsonBuilder builder) {

		LOG.debug("Scanning for Gson serializers");
		new ClasspathScanner<>("Serializer", JsonSerializer.class, ClassIdentityType.ASSIGNABLE_FROM).scanClasses()
				.forEach(c -> {
					registerTypeAdapters(builder, c);
				});

		LOG.debug("Scanning for Gson deserializers");
		new ClasspathScanner<>("Deserializer", JsonDeserializer.class, ClassIdentityType.ASSIGNABLE_FROM).scanClasses()
				.forEach(c -> {
					registerTypeAdapters(builder, c);
				});
	}

	private static void registerTypeAdapters(GsonBuilder builder, Class<?> typeAdapterClass) {

		Class<?> type = ClassUtils.getGenericRefs(typeAdapterClass.getClassLoader(), typeAdapterClass.getGenericSuperclass()).get(0);
		builder.registerTypeAdapter(type, ClassUtils.createInstance(typeAdapterClass));

		LOG.debug("==== " + ClassUtils.toString(type) + ": " + ClassUtils.toString(typeAdapterClass) + "====");
	}

	public static Gson getGson() {
		return instance;
	}

	public static <T> T fromJson(String json, Class<T> type) {
		return instance.fromJson(json, type);
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
	
	public static <V> Map<String, String> toMap(String obj) {
		return toMap(parse(obj));
	}

	public static JsonObject fromMap(Map<String, String> map) {
		
		JsonObject obj = new JsonObject();
		
		map.forEach((k,v) -> {
			obj.addProperty(k, k);
		});
		
		return obj;
	}
	
}
