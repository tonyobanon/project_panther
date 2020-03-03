package com.re.paas.api.fusion.ui.deprecated;

import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.SpiType;

public abstract class AbstractComponent extends AbstractResource {

	public AbstractComponent() {
		super(SpiType.UI_COMPONENT);
	}
	
	public static AbstractUIComponentDelegate getDelegate() {
		return Singleton.get(AbstractUIComponentDelegate.class);
	}

}
