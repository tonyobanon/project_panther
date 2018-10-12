package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.models.AbstractModelDelegate;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;

public class ModelSPILocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.MODEL;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("Model");
	}

	@Override
	public Class<?> classType() {
		return BaseModel.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractModelDelegate.class;
	}
	
}
