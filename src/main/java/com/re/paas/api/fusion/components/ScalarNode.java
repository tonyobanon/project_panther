package com.re.paas.api.fusion.components;

public final class ScalarNode<T> implements Node<T> {

	private T value;
	private VectorNode<?> parent;

	ScalarNode(T value) {
		this.value = value;
	}
	
	@Override
	public VectorNode<?> getParent() {
		return this.parent;
	}
	
	void setParent(VectorNode<?> parent) {
		this.parent = parent;
	}
	
	public T getValue() {
		return this.value;
	}
	
	void setValue(T value) {
		this.value = value;
	}

}
