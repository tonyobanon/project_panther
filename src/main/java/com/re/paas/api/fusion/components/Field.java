package com.re.paas.api.fusion.components;

public class Field<T> {
 
	private final String key;
	private Node<T> value;

	Field(String key, Node<T> value) {
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	
	public Node<T> getValue() {
		return value;
	}
	
	void setValue(Node<T> value) {
		this.value = value;
	}
}
