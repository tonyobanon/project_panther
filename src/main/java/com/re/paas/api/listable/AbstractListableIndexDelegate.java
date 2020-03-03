package com.re.paas.api.listable;

import java.util.Collection;
import java.util.Map;

import com.re.paas.api.runtime.spi.SpiDelegate;

public abstract class AbstractListableIndexDelegate extends SpiDelegate<ListableIndex<?>> {

	public abstract Collection<String> getNamespaces();
	
	public abstract Map<String, ListableIndex<?>> getListableIndexes(String namespace);
	
	public abstract String toString(ListableIndex<?> type);
	
	public abstract ListableIndex<?> fromString(String identifier);
	
}
