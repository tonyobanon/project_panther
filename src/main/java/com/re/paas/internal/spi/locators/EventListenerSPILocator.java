package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.events.AbstractEventDelegate;
import com.re.paas.api.events.EventListener;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.ClassIdentityType;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;

public class EventListenerSPILocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.EVENT_LISTENER;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("EventListener");
	}

	@Override
	public ClassIdentityType classIdentity() {
		return ClassIdentityType.ANNOTATION;
	}
	
	@Override
	public Class<?> classType() {
		return EventListener.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractEventDelegate.class;
	}

	
}
