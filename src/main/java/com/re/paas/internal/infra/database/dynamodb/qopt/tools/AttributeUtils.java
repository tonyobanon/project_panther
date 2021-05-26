package com.re.paas.internal.infra.database.dynamodb.qopt.tools;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.re.paas.api.annotations.develop.Todo;

public class AttributeUtils {
	
	private static final int OVERHEAD_SIZE_FOR_DYNAMODB_COLLECTIONS = 3;

	@Todo("This is only a prototype! Remember it denotes binary length")
	public
	static int binarySize(ByteBuffer b) {
		return b.array().length;
	}

	@Todo("This is only a prototype! Remember it denotes binary length")
	public static int numberSize(Number n) {
		// int charCode = Integer.parseInt(info, 2);
		return String.valueOf(n).length();
	}

	public static int stringSize(String s) {
		return s.length();
	}

	public static int getValueSize(Object o) {
	
		if (o == null || o instanceof Boolean) {
			return 1;
	
		} else if (o instanceof Number) {
			return numberSize((Number) o);
	
		} else if (o instanceof ByteBuffer) {
			return binarySize((ByteBuffer) o);
	
		} else if (o instanceof String) {
			return stringSize((String) o);
	
		} else if (o instanceof Collection) {
	
			Iterator<?> it = ((Collection<?>) o).iterator();
			int itemSize = 0;
	
			while (it.hasNext()) {
				itemSize += getValueSize(it.next());
			}
	
			return itemSize + AttributeUtils.OVERHEAD_SIZE_FOR_DYNAMODB_COLLECTIONS;
	
		} else if (o instanceof Map) {
	
			int itemSize = 0;
	
			@SuppressWarnings("unchecked")
			Set<Entry<String, AttributeValue>> mapEntries = ((Map<String, AttributeValue>) o).entrySet();
			for (Entry<String, AttributeValue> e : mapEntries) {
				itemSize += e.getKey().length() + getValueSize(e.getValue());
			}
	
			return itemSize + AttributeUtils.OVERHEAD_SIZE_FOR_DYNAMODB_COLLECTIONS;
		}
	
		return stringSize((String) o);
	}

	public static String toProjectionString(List<String> projections) {
	
		if (projections == null) {
			return null;
		}
	
		StringBuilder projectionExp = new StringBuilder();
		int size = projections.size();
	
		for (int i = 0; i < size; i++) {
			projectionExp.append(projections.get(i));
			if (i < size - 1) {
				projectionExp.append(", ");
			}
		}
		return projectionExp.toString();
	}

	@Todo("Verify that this regex works")
	public static List<String> getProjections(Object projectionExpression) {
		if (projectionExpression == null) {
			return new ArrayList<>();
		}
		String[] arr = ((String) projectionExpression).split("(\\s)*,(\\s)*");
		List<String> projections = new ArrayList<>(arr.length);
		for (String o : arr) {
			projections.add(o);
		}
		return projections;
	}
}
