package com.re.paas.api.runtime.spi;

import java.util.Map;

import com.re.paas.api.classes.AsyncDistributedMap;

public interface DelegateResorceSet {

	Map<Object, Object> getLocalStore();

	Map<String, AsyncDistributedMap<String, Object>> getDistributedStores();
	
}