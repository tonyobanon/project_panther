package com.re.paas.internal.components;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.fusion.BaseComponent;

public class Marshaller {

	public static String serialize(BaseComponent component) {
		return serialize0(component)
				.replaceAll("\\\\\"", "\"")
				.replaceAll("\"\\s*%%", "")
				.replaceAll("%%\\s*\"", "")
				.replaceAll("\\n", "");
	}
	
	private static Map<String, Object> toMap(BaseComponent component) {
		Map<String, Object> m = new HashMap<>();
		
		var excludedKeys = Arrays.asList("class", "declaringClass", "id", "assetId");
		
		try {
			Arrays
					.stream(Introspector.getBeanInfo(component.getClass()).getPropertyDescriptors())
					.filter(pd -> pd.getReadMethod() != null && !excludedKeys.contains(pd.getName()))
					.forEach(pd -> {
						try {
						m.put(pd.getName(), pd.getReadMethod().invoke(component));
						} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
							Exceptions.throwRuntime(e);
						}
					});;
					
		} catch (IntrospectionException e) {
			Exceptions.throwRuntime(e);
		}
		
		return m;
	}

	private static String serialize0(BaseComponent component) {

		GsonBuilder gsonBuilder = new GsonBuilder();
		ObjectWrapper<Gson> gson = new ObjectWrapper<>();

		JsonSerializer<BaseComponent> serializer = new JsonSerializer<BaseComponent>() {
			@Override
			public JsonElement serialize(BaseComponent src, Type typeOfSrc, JsonSerializationContext context) {
				
				String data = gson.get().toJson(toMap(src));

				return new JsonPrimitive(
						"""
						%% new components["$type"]({ id: "$id", input: $data, })%%"""
							.replace("$type", src.getClass().getSimpleName())
							.replace("$data", data)
							.replace("$id", src.getId())
						);
			}
		};

		gsonBuilder.registerTypeHierarchyAdapter(BaseComponent.class, serializer);
		gsonBuilder.registerTypeAdapterFactory(new EnumAdapterFactory());

		gson.set(gsonBuilder.create());

		Map<String, Object> data = toMap(component);
		data.put("id", component.getId());
		
		return gson.get().toJson(data);
	}

}
