package com.re.paas.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.runtime.RuntimeIdentity;
import com.re.paas.api.runtime.spi.SpiDelegateHandler;
import com.re.paas.api.utils.ClassUtils;

public class Singleton {

	private static final Map<String, Object> singletons = Collections.synchronizedMap(new HashMap<>());

	public static <T> T get(Class<T> type) {

		String name = ClassUtils.asString(type);

		@SuppressWarnings("unchecked")
		T o = (T) singletons.get(name);

		if (o != null && !ClassUtils.isAccessible(o.getClass())) {
			throw new SecurityException(name + " is not accessible by the current thread");
		}

		return o;
	}

	/**
	 * 
	 * <b>Implementation notes</b> <br>
	 * <p>
	 * <li>Since the current {@link SpiDelegateHandler} stores delegate instances as
	 * singletons, there is need to be able to register an already registered type.
	 * In light of this, only trusted callers are permitted to overwrite entries</li>
	 * </p>
	 * 
	 * @param type
	 * @param typeSubType
	 */
	public static <T> void register(Class<? extends T> type, T typeSubType) {

		String name = ClassUtils.asString(type);

		if (RuntimeIdentity.isExternalContext()) {

			if (singletons.containsKey(name)) {
				throw new SecurityException(name + " is already registered");
			}
		}

		singletons.put(name, typeSubType);
	}

}
