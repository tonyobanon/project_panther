package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.realms.AbstractRealmDelegate;
import com.re.paas.api.realms.Realm;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;

public class RealmSPILocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.REALM;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("Realm");
	}

	@Override
	public Class<?> classType() {
		return Realm.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractRealmDelegate.class;
	}
	
}
