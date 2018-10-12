package com.re.paas.api.listable;

import com.re.paas.api.designpatterns.Singleton;

public interface IndexedNameType {
	
	Integer id();
	
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
}
