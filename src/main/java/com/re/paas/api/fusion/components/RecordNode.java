package com.re.paas.api.fusion.components;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class RecordNode<T extends RecordNode<T>> implements VectorNode<T> {

	private VectorNode<?> parent;

	private Map<String, Field<?>> children = new HashMap<>();
	
	@Override
	public VectorNode<?> getParent() {
		return this.parent;
	}

	void setParent(VectorNode<?> parent) {
		this.parent = parent;
	}

	public Collection<Field<?>> getChildren() {
		return this.children.values();
	}

	protected <U> void addField(FieldInfo<U> fieldInfo) {
		this.children.put(fieldInfo.getKey(), new Field<U>(fieldInfo.getKey(), NodeUtil.getNodeFromPojo(this, null)));
	}

	protected <U> U getFieldValue(FieldGetHandle<U> handle) {

		@SuppressWarnings("unchecked")
		U value = (U) children.get(handle.getKey()).getValue();

		return value;
	}

	protected <U> void setFieldValue(FieldSetHandle<U> handle) {

		// handle.setValue(Util.createNode(handle.getValue()));

		// scan nodes, find by key...

//		fields.get(handle.getFieldName()).getSetter().accept(handle.getValue());

//		if (getComponent().getSessionId() != null) {
//			getComponent().addFuture(getComponent().getDelegate()
//					.setFieldValueInClient((FieldSetHandle) handle.setComponent(getComponent())));
//		}
	}
}
