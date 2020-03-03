package com.re.paas.api.runtime.spi;

import static com.re.paas.api.runtime.spi.TypeClassification.ACTIVE_RESOURCE;
import static com.re.paas.api.runtime.spi.TypeClassification.ACTIVE_DELEGATE;
import static com.re.paas.api.runtime.spi.TypeClassification.OPEN;;

public enum SpiType {

	CLOUD_ENVIRONMENT(ACTIVE_RESOURCE),
	ERROR, 
	NODE_ROLE(ACTIVE_DELEGATE),
	EVENT(ACTIVE_DELEGATE),
	MODEL(ACTIVE_DELEGATE),
	FUNCTION(ACTIVE_DELEGATE),
	CLUSTER_FUNCTION(ACTIVE_DELEGATE),
	TEMPLATE_OBJECT_MODEL,
	TEMPLATE_OBJECT_MODEL_FACTORY,
	TASK(ACTIVE_DELEGATE),
	SERVICE(ACTIVE_DELEGATE),
	UI_COMPONENT, 
	CLIENT_ASSET, 
	FUNCTIONALITY,
	REALM, 
	LISTABLE_INDEX, 
	CRYPTO_ADAPTER(ACTIVE_DELEGATE), 
	CACHE_ADAPTER(ACTIVE_DELEGATE), 
	DATABASE_ADAPTER(ACTIVE_DELEGATE),
	FILESYSTEM_ADAPTER(ACTIVE_DELEGATE), 
	METRICS_ADAPTER();

	private final TypeClassification classification;
	
	private final int count;

	private SpiType() {
		this(OPEN);
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
