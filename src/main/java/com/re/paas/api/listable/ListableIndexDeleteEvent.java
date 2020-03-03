package com.re.paas.api.listable;

import com.re.paas.api.events.BaseEvent;

public class ListableIndexDeleteEvent extends BaseEvent {

	private static final long serialVersionUID = 1L;
	private final ListableIndex<?> listable;
	
	public ListableIndexDeleteEvent(ListableIndex<?> listable) {
		this.listable = listable;
	}

	public ListableIndex<?> getListable() {
		return listable;
	}

}
