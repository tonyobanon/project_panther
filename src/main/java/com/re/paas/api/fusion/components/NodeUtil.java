package com.re.paas.api.fusion.components;

import com.google.common.primitives.Primitives;

public class NodeUtil {

	public static <T extends BaseComponent<T>> T getComponent(Node<?> value) {

		return null;
	}

	public static <T> T getPojoFromNode(Node<T> value) {
		if (value instanceof ScalarNode<?>) {
			return ((ScalarNode<T>) value).getValue();
		} else {
			@SuppressWarnings("unchecked")
			T r = (T) value;
			return r;
		}
	}

	static <T> Node<T> getNodeFromPojo(VectorNode<?> parent, T value) {
		
		// if value is already a node, we need to just setParent

		var isObject = !Primitives.isWrapperType(value.getClass()) && !(value instanceof String);

		// we need to visit to do 2 things
		// 1. replace collection instances
		// 2. set parent (while recursing children, we will halt if we encounter a node)

		return null;
	}

}
