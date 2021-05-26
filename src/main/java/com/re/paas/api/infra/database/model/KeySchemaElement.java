package com.re.paas.api.infra.database.model;


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

}
