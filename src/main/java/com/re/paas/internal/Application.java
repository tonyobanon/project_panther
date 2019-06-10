package com.re.paas.internal;

import java.lang.reflect.InvocationTargetException;

import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.infra.filesystem.FileSystemProviders;
import com.re.paas.internal.runtime.security.MetaFactory;

public abstract class Application {

	public static final String APPLICATION_NAME = "Compute Essentials";

	public static final String SOFTWARE_VENDOR_NAME = "Compute Essentials, Inc";
	public static final String SOFTWARE_VENDOR_EMAIL = "corporate@compute-essentials.com";

	private static final boolean performMetaFactoryScan = true;

	public static void main(String[] args)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, SecurityException, NoSuchMethodException {

		// Load custom file system provider
		FileSystemProviders.init();

		// Detect jvm flags
		Platform.readFlags(args);

		if (performMetaFactoryScan) {
			// Perform Meta factory scan
			MetaFactory.scan();
		}

		// Start application delegate
		ClassUtils.call(AppDelegate.class, ClassLoader.getSystemClassLoader());
	}

}
