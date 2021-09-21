package com.re.paas.api.runtime.spi;

import static com.re.paas.api.runtime.spi.TypeClassification.ACTIVE_DELEGATE;
import static com.re.paas.api.runtime.spi.TypeClassification.OPEN;;

public enum SpiType {

	ERROR, 
	EVENT(ACTIVE_DELEGATE),
	SCHEDULED_TASK(ACTIVE_DELEGATE),
	SERVICE(ACTIVE_DELEGATE),
	CRYPTO_ADAPTER(ACTIVE_DELEGATE), 
	CACHE_ADAPTER(ACTIVE_DELEGATE), 
	DATABASE_ADAPTER(ACTIVE_DELEGATE),
	FILESYSTEM_ADAPTER(ACTIVE_DELEGATE);

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
