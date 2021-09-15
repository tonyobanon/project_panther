package com.re.paas.internal.infra.database.dynamodb;

import com.re.paas.api.events.Subscribe;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.internal.infra.database.dynamodb.classes.DeleteItemEvent;
import com.re.paas.internal.infra.database.dynamodb.classes.PutItemEvent;

public class ItemChangeListener {

	private static TextSearchIndexHelper attrModel;

	private static TextSearchIndexHelper getIndexHelper() {
		if (attrModel == null) {
			attrModel = ((QueryInterfaceImpl) Database.get().getTextSearch().getQueryInterface()).getIndexHelper();
		}
		return attrModel;
	}
	
	@Subscribe
	public void onPutItem(PutItemEvent evt) {
		getIndexHelper().putValue(evt.getKey(), evt.getItem());
	}
	
	@Subscribe
	public void onDeleteItem(DeleteItemEvent evt) {
		getIndexHelper().deleteValue(evt.getKey());
	}

}
