package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.events.AbstractEventDelegate;
import com.re.paas.api.events.EventListener;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class EventListenerSPILocator extends BaseSPILocator {

	public EventListenerSPILocator() {
		addTypeSuffix("EventListener");
	}
	
	@Override
	public SpiType spiType() {
		return SpiType.EVENT_LISTENER;
	}
	
	@Override
	public Class<? extends Resource> classType() {
		return EventListener.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractEventDelegate.class;
	}

	
}
