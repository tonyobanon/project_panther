package com.re.paas.api.spi;

public abstract class BaseSPILocator {

	public abstract SpiTypes spiType();

	public abstract Iterable<String> classSuffix();

	public ClassIdentityType classIdentity() {
		return ClassIdentityType.ASSIGNABLE_FROM;
	}

	public abstract Class<?> classType();

	public abstract Class<? extends SpiDelegate<?>> delegateType();
	
	public ShuffleStrategy shuffleStrategy() {
		return ShuffleStrategy.HIGHER_DEPTH;
	}
	
	public enum ShuffleStrategy {
		HIGHER_DEPTH, LOWER_DEPTH
	}

}
