package com.re.paas.api.fusion.services;

import java.util.Collection;

import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiType;

public interface Functionality extends Resource {

	public static AbstractFunctionalityDelegate getDelegate() {
		return Singleton.get(AbstractFunctionalityDelegate.class);
	}
	
	public static Functionality getFunctionality(String namespace, Integer id) {
		return getDelegate().getFunctionality(namespace, id);
	}
	
	public static String toString(Functionality functionality) {
		return getDelegate().toString(functionality);
	}
	
	public static Functionality fromString(String functionalityString) {
		return getDelegate().fromString(functionalityString);
	}
	
	public static Collection<Functionality> all() {
		return getDelegate().all();
	}
	
	default String asString() {
		return toString(this);
	}
	
	public String getName();
	
	public int id();

	public Boolean isVisible();
	
	public String namespace();
	
	public Boolean isFrontend();
	
	public Boolean isBackend();
	
	public default Boolean requiresBasicAuth() {
		return id() >= 0;
	}
	
	public default Boolean requiresAuth() {
		return id() > 0;
	}
	
	@Override
	default SpiType getSpiType() {
		return SpiType.FUNCTIONALITY;
	}

}
