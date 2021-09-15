package com.re.paas.api.infra.database.model;

import java.util.Collection;

public class KeySchemaElement {

	private String attributeName;

	private KeyType keyType;


	public KeySchemaElement(String attributeName, KeyType keyType) {
		setAttributeName(attributeName);
		setKeyType(keyType);
	}

	public KeySchemaElement setAttributeName(String attributeName) {
		this.attributeName = attributeName;
		return this;
	}

	public String getAttributeName() {
		return this.attributeName;
	}

	public KeyType getKeyType() {
		return this.keyType;
	}

	public KeySchemaElement setKeyType(KeyType keyType) {
		this.keyType = keyType;
		return this;
	}
	
	private static KeySchemaElement getSchemaElement(Collection<KeySchemaElement> keys, KeyType type) {
		for (KeySchemaElement e : keys) {
			if (e.getKeyType() == type) {
				return e;
			}
		}
		return null;
	}
	
	public static String getHashKey(Collection<KeySchemaElement> keys) {
		return getSchemaElement(keys, KeyType.HASH).getAttributeName();
	}
	
	public static String getRangeKey(Collection<KeySchemaElement> keys) {
		return getSchemaElement(keys, KeyType.RANGE).getAttributeName();
	}
	
}
