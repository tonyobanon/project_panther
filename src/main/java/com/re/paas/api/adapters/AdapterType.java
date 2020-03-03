package com.re.paas.api.adapters;

import com.re.paas.api.Adapter;
import com.re.paas.api.cryto.AbstractCryptoAdapterDelegate;
import com.re.paas.api.infra.cache.AbstractCacheAdapterDelegate;
import com.re.paas.api.infra.database.AbstractDatabaseAdapterDelegate;
import com.re.paas.api.infra.filesystem.AbstractFileSystemAdapterDelegate;
import com.re.paas.api.infra.metrics.AbstractMetricsAdapterDelegate;

/**
 * 
 * This enum contains the delegate who resources consists of system adapters
 * 
 * @author anthony.anyanwu
 */
public enum AdapterType {

	CACHE(AbstractCacheAdapterDelegate.class), DATABASE(AbstractDatabaseAdapterDelegate.class),
	FILE_SYSTEM(AbstractFileSystemAdapterDelegate.class), CRYPTO(AbstractCryptoAdapterDelegate.class),
	METRICS(AbstractMetricsAdapterDelegate.class);

	private final Class<? extends AbstractAdapterDelegate<?, ? extends Adapter<?>>> delegateType;

	private AdapterType(Class<? extends AbstractAdapterDelegate<?, ? extends Adapter<?>>> delegateType) {
		this.delegateType = delegateType;
	}

	public Class<? extends AbstractAdapterDelegate<?, ? extends Adapter<?>>> getDelegateType() {
		return delegateType;
	}

	public static AdapterType from(String type) {

		for (AdapterType t : AdapterType.values()) {
			if (t.toString().equals(type)) {
				return t;
			}
		}

		return null;
	}

}
