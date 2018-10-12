package com.re.paas.api.events;

import java.util.function.Consumer;

import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.spi.SpiDelegate;

public abstract class AbstractEventDelegate extends SpiDelegate<Object> {
	
	public static AbstractEventDelegate getInstance() {
		return Singleton.get(AbstractEventDelegate.class);
	}

	@Override
	public final Class<?> getLocatorClassType() {
		return EventListener.class;
	}
	
	public abstract <T extends BaseEvent> void one(Class<T> eventType, Consumer<T> consumer);

	public abstract void dispatch(BaseEvent evt);

	public abstract void dispatch(BaseEvent evt, boolean isAsync);
	
}
