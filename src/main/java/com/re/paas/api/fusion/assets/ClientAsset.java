package com.re.paas.api.fusion.assets;

import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.SpiType;

public final class ClientAsset extends AbstractResource {

	protected ClientAsset() {
		super(SpiType.CLIENT_ASSET);
	}
	
	public static AbstractClientAssetDelegate getDelegate() {
		return Singleton.get(AbstractClientAssetDelegate.class);
	}
}
