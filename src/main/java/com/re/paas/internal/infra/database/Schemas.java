package com.re.paas.internal.infra.database;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.re.paas.api.classes.ParameterizedClass;
import com.re.paas.api.infra.database.model.BaseTable;
import com.re.paas.api.utils.ClassUtils;

import static com.re.paas.internal.infra.database.DynamoDBConstants.AttributeTypes.*;
import static com.re.paas.internal.infra.database.DynamoDBConstants.MAX_NESTED_COLLECTION_DEPTH;

public class Schemas {

	public static Map<String, String> generate(Class<? extends BaseTable> model) {

		var result = new HashMap<String, String>();

		for (Field field : model.getDeclaredFields()) {

			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			// This is an instance field, so we assume that this represents a table
			// attribute
			result.put(field.getName(), getDBType(model.getClassLoader(), field));
		}

		return result;
	}

	private static String getSimpleTypeNamer(String typeName) {
		switch (typeName) {
		case "java.lang.String":
			return S;
		case "java.lang.Number":
			return N;
		case "java.nio.ByteBuffer":
			return B;
		case "java.lang.Boolean":
			return BOOL;
		case "java.util.Map":
			return M;
		case "java.util.List":
			return L;
		case "java.util.Set":
			return S;
		}
		
		throw new IllegalArgumentException("Unknown type: " + typeName);
	}
	
	private static String getComplexTypeNamer(String typeName) {
		// SS, NS, BS
		switch (typeName) {
		case "S<S>":
			return SS;
		case "S<N>":
			return NS;
		case "S<B>":
			return BS;
		default:
			return typeName;
		}
	}

	/**
	 * Note: this does not handle data type: NULL
	 * 
	 * @param typeName
	 * @return
	 */
	private static String getDBType(ClassLoader cl, Field field) {

		ParameterizedClass c = ClassUtils.getParameterizedClass(cl, field.getGenericType().getTypeName());

		Class<?>[] allTypes = new Class<?>[] { String.class, Number.class, ByteBuffer.class, Boolean.class, Map.class,
				List.class, Set.class };

		Class<?>[] scalarTypes = new Class<?>[] { String.class, Number.class, ByteBuffer.class };

		@SuppressWarnings("unchecked")
		List<Class<?>> l = Collections.EMPTY_LIST;
		
		Map<Class<?>, List<Class<?>>> allowedTypes = Map.of(
				String.class, l, 
				Number.class, l, 
				ByteBuffer.class, l, 
				Boolean.class, l, 
				Map.class, Arrays.asList(allTypes), 
				List.class, Arrays.asList(allTypes),
				Set.class, Arrays.asList(scalarTypes)
		);

		return c.asString(
				0, allowedTypes, 
				Schemas::getSimpleTypeNamer, Schemas::getComplexTypeNamer, 
				true, true, MAX_NESTED_COLLECTION_DEPTH
		);
	}

}
