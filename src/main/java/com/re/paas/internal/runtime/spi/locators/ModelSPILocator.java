package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.models.AbstractModelDelegate;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class ModelSPILocator extends BaseSPILocator {

	public ModelSPILocator() {
		addTypeSuffix("Model");
	}
	
	@Override
	public SpiType spiType() {
		return SpiType.MODEL;
	}

	@Override
	public Class<? extends AbstractResource> classType() {
		return BaseModel.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractModelDelegate.class;
	}
	
}
