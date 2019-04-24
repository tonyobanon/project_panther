package com.re.paas.api.events;

import java.io.Serializable;
import java.util.function.Consumer;

public abstract class BaseEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String eventId;
	
	public static <T extends BaseEvent> void one(Class<T> eventType, Consumer<T> consumer) {
		AbstractEventDelegate.getInstance().one(eventType, consumer);
	}
	
	public static void dispatch(BaseEvent evt) {
		AbstractEventDelegate.getInstance().dispatch(evt);
	}

	public String getEventId() {
		return eventId;
	}
	
	public BaseEvent setEventId(String eventId) {
		this.eventId = eventId;
		return this;
	}

	public abstract String name();
	
}
