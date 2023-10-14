package com.re.paas.internal.fusion.components;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class EnumAdapterFactory implements TypeAdapterFactory {

	@Override
	public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
		Class<? super T> rawType = type.getRawType();
		if (rawType.isEnum()) {
			return new EnumTypeAdapter<T>();
		}
		return null;
	}

	private class EnumTypeAdapter<T> extends TypeAdapter<T> {
		@Override
		public void write(JsonWriter out, T value) throws IOException {
			if (value == null || !value.getClass().isEnum()) {
				out.nullValue();
				return;
			}

			try {
				PropertyDescriptor pd = Arrays
						.stream(Introspector.getBeanInfo(value.getClass()).getPropertyDescriptors())
						.filter(p -> {
							return p.getName().equals("value");
						}).findFirst().get();

				String val = String.valueOf(pd.getReadMethod().invoke(value));

				out.value(val);
			} catch (IntrospectionException | IllegalAccessException | InvocationTargetException | IOException e) {
				e.printStackTrace();
			}
		}

		public T read(JsonReader in) throws IOException {
			throw new UnsupportedOperationException();
		}
	}
}