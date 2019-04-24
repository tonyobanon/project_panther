package com.re.paas.api.listable;

import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiType;

public interface IndexedNameType extends Resource {

	abstract Integer id();
	
	String namespace();
	
	static AbstractIndexedNameTypeDelegate getDelegate() {
		return Singleton.get(AbstractIndexedNameTypeDelegate.class);
	}
	
	static IndexedNameType fromString(String typeString) {
		return getDelegate().fromString(typeString);
	}
	
	default String asString() {
		return getDelegate().toString(this);
	}
	
	@Override
	default SpiType getSpiType() {
		return SpiType.INDEXED_NAME_TYPE ;
	}
}
