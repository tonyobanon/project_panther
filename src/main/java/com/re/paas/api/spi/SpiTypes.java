package com.re.paas.api.spi;

import static com.re.paas.api.spi.TypeClassification.ACTIVE;
import static com.re.paas.api.spi.TypeClassification.PASSIVE;;

public enum SpiTypes {

	CLOUD_ENVIRONMENT, ERROR, NODE_ROLE(ACTIVE),
	EVENT_LISTENER(ACTIVE), MODEL(ACTIVE),
	FUNCTION, CLUSTER_FUNCTION, TEMPLATE_OBJECT_MODEL,
	TEMPLATE_OBJECT_MODEL_FACTORY, TASK_MODEL, LISTABLE,
	SERVICE, UI_COMPONENT, FUNCTIONALITY,
	REALM, INDEXED_NAME_TYPE;

	private final TypeClassification classification;
	private final int count;

	private SpiTypes() {
		this(PASSIVE);
	}

	private SpiTypes(TypeClassification classification) {
		this(-1, classification);
	}

	private SpiTypes(int count, TypeClassification classification) {
		this.count = count;
		this.classification = classification;
	}

	public int getCount() {
		return count;
	}

	public TypeClassification classification() {
		return classification;
	}

}
