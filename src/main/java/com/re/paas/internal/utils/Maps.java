package com.re.paas.internal.utils;

import java.util.Map;

public class Maps {

	public static <K,V> V first(Map<K, V> map) {
		for(V v : map.values()) {
			return v;
		}
		return null;
	}
	
}
