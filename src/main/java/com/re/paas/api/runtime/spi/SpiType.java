package com.re.paas.api.runtime.spi;

import static com.re.paas.api.runtime.spi.TypeClassification.ACTIVE;
import static com.re.paas.api.runtime.spi.TypeClassification.PASSIVE;;

public enum SpiType {

	CLOUD_ENVIRONMENT(ACTIVE), ERROR, NODE_ROLE(ACTIVE),
	EVENT_LISTENER(ACTIVE), MODEL(ACTIVE),
	FUNCTION, CLUSTER_FUNCTION, TEMPLATE_OBJECT_MODEL,
	TEMPLATE_OBJECT_MODEL_FACTORY, TASK_IMAGE(ACTIVE), LISTABLE,
	SERVICE(ACTIVE), UI_COMPONENT, FUNCTIONALITY,
	REALM, INDEXED_NAME_TYPE, CRYPTO_ADAPTER(ACTIVE), CACHE_ADAPTER(ACTIVE), DATABASE_ADAPTER(ACTIVE),
	FILESYSTEM_ADAPTER(ACTIVE);

	private final TypeClassification classification;
	private final int count;

	private SpiType() {
		this(PASSIVE);
	}

	private SpiType(TypeClassification classification) {
		this(-1, classification);
	}

	private SpiType(int count, TypeClassification classification) {
		this.count = count;
		this.classification = classification;
	}

	public int getCount() {
		return count;
	}

	public TypeClassification classification() {
		return classification;
	}
	
	@Override
	public String toString() {
		return name().toLowerCase();
	}

}
