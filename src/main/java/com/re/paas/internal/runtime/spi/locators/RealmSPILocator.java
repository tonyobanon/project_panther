package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.realms.AbstractRealmDelegate;
import com.re.paas.api.realms.Realm;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class RealmSPILocator extends BaseSPILocator {
	
	@Override
	public SpiType spiType() {
		return SpiType.REALM;
	}

	@Override
	public Class<? extends AbstractResource> classType() {
		return Realm.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractRealmDelegate.class;
	}
	
}
