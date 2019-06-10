package com.re.paas.api.events;

import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiType;

public interface EventListener extends Resource {

	@Override
	default SpiType getSpiType() {
		return SpiType.EVENT;
	}

}
