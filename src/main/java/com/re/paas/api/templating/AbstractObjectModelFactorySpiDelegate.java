package com.re.paas.api.templating;

import java.util.Map;

import com.re.paas.api.runtime.spi.SpiDelegate;

public abstract class AbstractObjectModelFactorySpiDelegate
		extends SpiDelegate<TemplateObjectModelFactory<? extends TemplateObjectModel>> {

	public abstract Map<Class<?>, TemplateObjectModelFactory<? extends TemplateObjectModel>> resources();

}
