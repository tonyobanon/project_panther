package com.re.paas.api.crytography;

import java.util.Map;

import com.re.paas.api.Adapter;
import com.re.paas.api.Singleton;
import com.re.paas.api.adapters.AdapterType;
import com.re.paas.api.runtime.spi.SpiType;

public interface CryptoAdapter extends Adapter<CryptoProvider> {

	public static AbstractCryptoAdapterDelegate getDelegate() {
		return Singleton.get(AbstractCryptoAdapterDelegate.class);
	}
	
	default CryptoProvider getProvider(Map<String, String> fields) {
		return getResource(fields);
	}
	
	@Override
	default AdapterType getType() {
		return AdapterType.CRYPTO;
	}
	
	@Override
	default SpiType getSpiType() {
		return SpiType.CRYPTO_ADAPTER;
	}
}