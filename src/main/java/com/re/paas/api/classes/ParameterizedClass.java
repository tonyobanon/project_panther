package com.re.paas.api.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.re.paas.api.utils.ClassUtils;

public class ParameterizedClass {

	private final Class<?> type;
	private List<ParameterizedClass> genericTypes = new ArrayList<>();

	public ParameterizedClass(Class<?> type) {
		this.type = type;
	}

	public Class<?> getType() {
		return type;
	}

	public List<ParameterizedClass> getGenericTypes() {
		return genericTypes;
	}

	public ParameterizedClass setGenericTypes(List<ParameterizedClass> genericTypes) {
		this.genericTypes = genericTypes;
		return this;
	}

	public ParameterizedClass addGenericType(ParameterizedClass genericType) {
		this.genericTypes.add(genericType);
		return this;
	}

	private static Map<Class<?>, Integer> getGenericTypeCounts() {
		var genericTypeCounts = new HashMap<Class<?>, Integer>(3);

		genericTypeCounts.put(List.class, 1);
		genericTypeCounts.put(Set.class, 1);
		genericTypeCounts.put(Map.class, 2);

		return genericTypeCounts;
	}
	
	private static List<Class<?>> getMapKeysScalarTypes() {
		return List.of(String.class, Number.class);
	}

	public String asString(
			Integer depth, Map<Class<?>, List<Class<?>>> allowedTypes, 
			Function<String, String> simpleTypeNamer, Function<String, String> complexTypeNamer,
			Boolean ensureGenericTypes,
			Boolean ensureScalarMapKeys, Integer maxDepth) {

		if (depth > maxDepth) {
			throw new IllegalArgumentException("Maximum depth reached: " + maxDepth);
		}

		StringBuilder sb = new StringBuilder();

		if (!allowedTypes.containsKey(getType())) {
			throw new IllegalArgumentException(getType() + " is not allowed");
		}
		
		if (ensureGenericTypes) {

			Integer expectedGenericCount = getGenericTypeCounts().get(getType());

			if ((expectedGenericCount != null ? expectedGenericCount : 0) != this.genericTypes.size()) {
				throw new IllegalArgumentException(
						getType() + " should have " + expectedGenericCount + " generic type(s) instead of " + this.genericTypes.size());
			}
		}

		sb.append(simpleTypeNamer.apply(ClassUtils.getName(getType())));

		if (!this.genericTypes.isEmpty()) {
			
			sb.append("<");

			for (int i = 0; i < this.genericTypes.size(); i++) {

				if (i > 0) {
					sb.append(", ");
				}

				ParameterizedClass t = this.genericTypes.get(i);

				sb.append(t.asString(depth + 1, allowedTypes, simpleTypeNamer, complexTypeNamer, ensureGenericTypes, ensureScalarMapKeys, maxDepth));

				if (getType().equals(Map.class) && i == 0 && ensureScalarMapKeys) {
					
					if (getMapKeysScalarTypes().contains(t.getType())) {
						
						continue;
					} else {
						
						throw new IllegalArgumentException("Unknown <K> type: " + t.getType() + " for " + getType());
					}
				}

				List<Class<?>> allowedGenericTypes = allowedTypes.get(getType());

				if (allowedGenericTypes != null && !allowedGenericTypes.contains(t.getType())) {
					throw new IllegalArgumentException("Unknown generic type: " + t.getType() + " for " + getType());
				}
			}

			sb.append(">");
		}

		return complexTypeNamer.apply(sb.toString());
	}

}
