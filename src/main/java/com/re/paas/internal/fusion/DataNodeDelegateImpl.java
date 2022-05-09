package com.re.paas.internal.fusion;

import java.util.Map;

import com.re.paas.api.fusion.DataNodeDelegate;
import com.re.paas.api.infra.cache.Cache;
import com.re.paas.api.infra.cache.CacheAdapter;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;

public class DataNodeDelegateImpl implements DataNodeDelegate {
	
	// private static final Logger LOG = LoggerFactory.get().getLog(DataNodeDelegateImpl.class);
	
	@Override
	public Object getProperty(String sessionId, String path, String type) {
		
		// If type is Map or List, we do not have to fetch type from client, but we can
		// direction instantiate the collection impl class, passing in the path as well
		// as the generic type
		
		// If type == 'boolean' || 'Double' || 'String', fetch value from client
		
		// If neither of the above, it could be either a component or an object.
		// For this we need to ask the client
		

		// Add code in setter and getter to prevent mistaken manipulations to the data
		// instead of waiting until an error is thrown by the serializer, because then,
		// it will lack context
		
		return null;
	}

	@Override
	public void setProperty(String sessionId, String path, Object value) {
		
		
		ClientSessionSocketHandler.getSession(sessionId);
		
	}
	
	private static Cache<String, Object> getClientDataStore() {
		return CacheAdapter.getDelegate().getCacheFactory().get("client_data");
	}

	@Override
	public String toPropertyString(String sessionId, String path) {
		
		
		
		return null;
	}
	
	static void clearClientData(String sessionId) {
		Cache<String, Object> cache = getClientDataStore();
		cache.del(sessionId);
	}
	
	static void updateClientData(String sessionId, Map<String, String> data) {
		Cache<String, Object> cache = getClientDataStore();
		
		data.forEach((k, v) -> {
			cache.hset(sessionId, k, v);
		});
	}
	
}
