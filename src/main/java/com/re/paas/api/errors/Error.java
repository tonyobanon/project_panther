package com.re.paas.api.errors;

import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiType;

/**
 * All enums implementing this interface should at least contain a static method
 * from(int)
 */
public interface Error extends Resource {

	public int getCode();

	public String getMessage();

	public boolean isFatal();
	
	public String namespace();
	
	@Override
	default SpiType getSpiType() {
		return SpiType.ERROR;
	}

}
