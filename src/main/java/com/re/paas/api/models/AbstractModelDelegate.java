package com.re.paas.api.models;

import java.util.Collection;

import com.re.paas.api.runtime.spi.SpiDelegate;

public abstract class AbstractModelDelegate extends SpiDelegate<BaseModel> {

	public abstract Collection<BaseModel> getModels();
	
}
