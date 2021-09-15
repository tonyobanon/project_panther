package com.re.paas.internal.infra.database.dynamodb.classes;

import com.re.paas.api.events.BaseEvent;
import com.re.paas.api.infra.database.document.Item;

public class GetItemEvent extends BaseEvent {

	private static final long serialVersionUID = 1L;
	
	private final TablePrimaryKey key;
	private final Item item;

	public GetItemEvent(TablePrimaryKey key, Item item) {
		this.key = key;
		this.item = item;
	}

	public TablePrimaryKey getKey() {
		return key;
	}
	
	public Item getItem() {
		return item;
	}
}
