package com.re.paas.api.runtime.spi;

public abstract class AbstractResource implements Resource {

	private final SpiType spiType;

	protected AbstractResource(SpiType spiType) {
		super();
		this.spiType = spiType;
	}
	
	@Override
	public SpiType getSpiType() {
		return spiType;
	}
}
