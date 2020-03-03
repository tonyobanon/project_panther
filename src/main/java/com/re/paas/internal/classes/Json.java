package com.re.paas.internal.classes;

import java.text.DateFormat;
import java.util.Map;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.runtime.spi.ClassIdentityType;
import com.re.paas.api.utils.ClassUtils;

@BlockerTodo("Use the JsonParse.get() instead of calling this directly")
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

		Class<?> type = ClassUtils.getParameterizedClass(typeAdapterClass.getClassLoader(), typeAdapterClass.getGenericInterfaces()[0]).getGenericTypes().get(0).getType();
		builder.registerTypeAdapter(type, com.re.paas.internal.classes.ClassUtil.createInstance(typeAdapterClass));

		LOG.debug("==== " + ClassUtils.toString(type) + ": " + ClassUtils.toString(typeAdapterClass) + "====");
	}

	@BlockerTodo("Make this method private")
	public static Gson getGson() {
		return instance;
	}
	
	public static <T> String toJson(T o) {
		return instance.toJson(o);
	}

	public static <T> T fromJson(String json, Class<T> type) {
		return instance.fromJson(json, type);
	}
	
	public static <T> T fromMap(Map<String, Object> map, Class<T> type) {
		return fromJson(new JsonObject(map).toString(), type);
	}

	
	public static <T, V> Map<String, Object> toMap(T obj) {
		return new JsonObject(instance.toJson(obj)).getMap();
	}
	
}
