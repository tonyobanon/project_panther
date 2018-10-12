package com.re.paas.api.models.classes;

import com.re.paas.api.classes.ClientResources;

public class IndexedNameSpec {
	
	private String key;
	
	private String x;
	private String y;
	private String z;
	
	public IndexedNameSpec() {
	}
	
	public IndexedNameSpec(String key, String x) {
		this(key, x, null, null);
	}
	
	public IndexedNameSpec(String key, String x, String y) {
		this(key, x, y, null);
	}
	
	public IndexedNameSpec(String key, String x, String y, String z) {
		this.key = key;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static IndexedNameSpec get(String key, String x) {
		return get(key, x, null, null);
	}
	
	public static IndexedNameSpec get(String key, String x, String y) {
		return get(key, x, y, null);
	}
	
	public static IndexedNameSpec get(String key, String x, String y, String z) {
		return new IndexedNameSpec(key, x, y, z);
	}

	public String getKey() {
		return key;
	}

	public IndexedNameSpec setKey(String key) {
		this.key = key;
		return this;
	}

	public String getX() {
		return x;
	}

	public IndexedNameSpec setX(String x) {
		this.x = x;
		return this;
	}
	
	public String getY() {
		return y;
	}

	public IndexedNameSpec setY(String y) {
		this.y = y;
		return this;
	}
	

	public String getZ() {
		return z;
	}

	public IndexedNameSpec setZ(String z) {
		this.z = z;
		return this;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder().append(x);
		String separator = ClientResources.HtmlCharacterEntities.SPACE.toString();
		
		if(y != null) {
			sb.append(separator)
				.append(y);
		}
		
		if(z != null) {
			sb.append(separator)
				.append(z);
		}
		
		return sb.toString();
	}

}
