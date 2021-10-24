package com.re.paas.internal.runtime.spi;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.re.paas.api.Platform;

public abstract class Application {

	public static final String APPLICATION_NAME = "Compute Essentials";

	public static final String SOFTWARE_VENDOR_NAME = "Compute Essentials, Inc";
	public static final String SOFTWARE_VENDOR_EMAIL = "corporate@compute-essentials.com";

	public static final Boolean DEV_MODE = true;

	private static final boolean performMetaFactoryScan = true;

	public static void main(String[] args)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, SecurityException, NoSuchMethodException, IOException {

		// Detect jvm flags
		Platform.readFlags(args);

		if (performMetaFactoryScan) {

			// Perform Meta factory scan
			// RuntimeTransformers.apply();
		}

		// Start application delegate
		com.re.paas.internal.classes.ClassUtil.call(AppDelegate.class, ClassLoader.getSystemClassLoader());
	}

}
