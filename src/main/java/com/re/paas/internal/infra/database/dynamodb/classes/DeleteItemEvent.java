package com.re.paas.internal.infra.database.dynamodb.classes;

import com.re.paas.api.events.BaseEvent;

public class DeleteItemEvent extends BaseEvent {
	
	private static final long serialVersionUID = 1L;

	private final TablePrimaryKey key;

	public DeleteItemEvent(TablePrimaryKey key) {
		this.key = key;
	}
	
	public TablePrimaryKey getKey() {
		return key;
	}
}
