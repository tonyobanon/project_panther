package com.re.paas.api.infra.database.model;

import com.re.paas.api.infra.database.document.Item;

public class PutItemResult {
	
	private Item item;

	public Item getItem() {
		return item;
	}

	public PutItemResult setItem(Item item) {
		this.item = item;
		return this;
	}
	
}
