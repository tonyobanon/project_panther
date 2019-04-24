package com.re.paas.api.errors;

import com.re.paas.api.runtime.spi.SpiDelegate;

public abstract class AbstractErrorSpiDelegate extends SpiDelegate<Error> {
	
	public abstract String getError(String namespace, Integer code);
	
}
