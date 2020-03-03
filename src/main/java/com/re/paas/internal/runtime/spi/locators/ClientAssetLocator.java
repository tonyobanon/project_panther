package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.fusion.assets.AbstractClientAssetDelegate;
import com.re.paas.api.fusion.assets.ClientAsset;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class ClientAssetLocator extends BaseSPILocator {

	@Override
	public SpiType spiType() {
		return SpiType.CLIENT_ASSET;
	}

	@Override
	public Class<? extends AbstractResource> classType() {
		return ClientAsset.class;
	}

	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractClientAssetDelegate.class;
	}

	@Override
	public Boolean scanResourceClasses() {
		return false;
	}
}
