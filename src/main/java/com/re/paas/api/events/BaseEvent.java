package com.re.paas.api.events;

import java.io.Serializable;
import java.util.function.Consumer;

import com.re.paas.api.utils.Utils;

public abstract class BaseEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String eventId;
	
	public BaseEvent() {
		this.eventId = Utils.newShortRandom();
	}
	
	public static AbstractEventDelegate getDelegate() {
		return AbstractEventDelegate.getInstance();
	}
	
	public static <T extends BaseEvent> void one(Class<T> eventType, Consumer<T> consumer) {
		getDelegate().one(eventType, consumer);
	}
	
	public static void dispatch(BaseEvent evt) {
		getDelegate().dispatch(evt);
	}

	public String getEventId() {
		return eventId;
	}
	
	public BaseEvent setEventId(String eventId) {
		this.eventId = eventId;
		return this;
	}

	public String name() {
		return this.getClass().getSimpleName();
	}
	
}
