package com.re.paas.api.listable;

import java.util.function.Consumer;

import com.re.paas.api.spi.SpiDelegate;

public abstract class AbstractListableDelegate extends SpiDelegate<Listable<?>> {

	public abstract void forEachSearchable(Consumer<IndexedNameType> consumer);
	
	public abstract Listable<?> getListable(IndexedNameType type);
	
	public abstract SearchableUISpec getSearchable(IndexedNameType type);
	
}
