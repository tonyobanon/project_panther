package com.re.paas.api.classes;

import java.util.ArrayList;
import java.util.List;

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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(getType().getName());

		if (!genericTypes.isEmpty()) {
			sb.append(": [");
			for (int i = 0; i < genericTypes.size(); i++) {
				
				if(i > 0) {
					sb.append(", ");
				}
				
				ParameterizedClass t = genericTypes.get(i);
				sb.append(t.toString());
			}
			sb.append("]");
		}
		
		sb.append("}");
		return sb.toString();
	}

}
