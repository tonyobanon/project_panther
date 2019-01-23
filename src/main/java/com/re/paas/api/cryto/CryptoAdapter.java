package com.re.paas.api.cryto;

import java.util.Map;

import com.re.paas.api.Adapter;
import com.re.paas.api.adapters.AdapterType;
import com.re.paas.api.designpatterns.Singleton;

public interface CryptoAdapter extends Adapter {

	public static AbstractCryptoAdapterDelegate getDelegate() {
		return Singleton.get(AbstractCryptoAdapterDelegate.class);
	}
	
	CryptoProvider getProvider(Map<String, String> fields);
	
	@Override
	default AdapterType getType() {
		return AdapterType.CRYPTO;
	}
}
