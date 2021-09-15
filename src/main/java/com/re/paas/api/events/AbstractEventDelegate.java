package com.re.paas.api.events;

import java.util.function.Consumer;

import com.re.paas.api.Singleton;
import com.re.paas.api.runtime.spi.SpiDelegate;

public abstract class AbstractEventDelegate extends SpiDelegate<EventListener> {
	
	public static AbstractEventDelegate getInstance() {
		return Singleton.get(AbstractEventDelegate.class);
	}
	
	public abstract <T extends BaseEvent> void one(Class<T> eventType, Consumer<T> consumer);

	public abstract <T extends BaseEvent> void dispatch(T evt);

	public abstract <T extends BaseEvent> void dispatch(T evt, boolean isAsync);
	
}
