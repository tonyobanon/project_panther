package com.re.paas.internal;

import java.lang.reflect.InvocationTargetException;

import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.infra.filesystem.FileSystemProviders;

public class Application {

	public static final String APPLICATION_NAME = "Compute Essentials";

	public static final String SOFTWARE_VENDOR_NAME = "Compute Essentials, Inc";
	public static final String SOFTWARE_VENDOR_EMAIL = "corporate@compute-essentials.com";


	public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchMethodException {
		 
		// Load custom file system provider
		FileSystemProviders.init();
		
		// Detect jvm flags
		Platform.readFlags(args);
		
		// Enforce protection context
		CodeSecurity.scanProtectionContext();
		
		// Start application delegate
		ClassUtils.call(AppDelegate.class, ClassLoader.getSystemClassLoader());
	}

}
