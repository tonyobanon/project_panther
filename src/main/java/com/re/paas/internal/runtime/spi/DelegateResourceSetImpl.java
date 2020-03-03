package com.re.paas.internal.runtime.spi;

import java.util.Map;

import com.re.paas.api.classes.AsyncDistributedMap;
import com.re.paas.api.runtime.spi.DelegateResorceSet;

public class DelegateResourceSetImpl implements DelegateResorceSet {

	private final Map<String, Map<String, Object>> distributedStores;
	private final Map<Object, Object> localStore;

	public DelegateResourceSetImpl(Map<Object, Object> localStore,
			Map<String, Map<String, Object>> distributedStores) {
		this.distributedStores = distributedStores;
		this.localStore = localStore;
	}

	@Override
	public Map<Object, Object> getLocalStore() {
		return localStore;
	}

	@Override
	public Map<String, Map<String, Object>> getDistributedStores() {
		return distributedStores;
	}
}
