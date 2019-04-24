package com.re.paas.api.listable;

import com.re.paas.api.runtime.spi.SpiDelegate;

public abstract class AbstractIndexedNameTypeDelegate extends SpiDelegate<IndexedNameType> {

	public abstract String toString(IndexedNameType type);
	
	public abstract IndexedNameType fromString(String typeString);
	
}
