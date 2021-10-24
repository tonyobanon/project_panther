package com.re.paas.api.adapters;

import com.re.paas.api.Adapter;
import com.re.paas.api.crytography.AbstractCryptoAdapterDelegate;
import com.re.paas.api.infra.cache.AbstractCacheAdapterDelegate;
import com.re.paas.api.infra.database.AbstractDatabaseAdapterDelegate;
import com.re.paas.api.infra.filesystem.AbstractFileSystemAdapterDelegate;

/**
 * 
 * This enum contains the delegate who resources consists of system adapters
 * 
 * @author anthony.anyanwu
 */
public enum AdapterType {

	CACHE(AbstractCacheAdapterDelegate.class, true), DATABASE(AbstractDatabaseAdapterDelegate.class, true),
	FILE_SYSTEM(AbstractFileSystemAdapterDelegate.class, false), CRYPTO(AbstractCryptoAdapterDelegate.class, false);

	private final Boolean isPlatformIntrinsic;
	private final Class<? extends AbstractAdapterDelegate<?, ? extends Adapter<?>>> delegateType;

	private AdapterType(Class<? extends AbstractAdapterDelegate<?, ? extends Adapter<?>>> delegateType, Boolean isPlatformIntrinsic) {
		this.delegateType = delegateType;
		this.isPlatformIntrinsic = isPlatformIntrinsic;
	}

	public Class<? extends AbstractAdapterDelegate<?, ? extends Adapter<?>>> getDelegateType() {
		return delegateType;
	}

	public Boolean isPlatformIntrinsic() {
		return isPlatformIntrinsic;
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
