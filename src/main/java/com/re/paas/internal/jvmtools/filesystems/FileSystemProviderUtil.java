package com.re.paas.internal.jvmtools.filesystems;

import java.lang.reflect.Field;
import java.nio.file.spi.FileSystemProvider;

public class FileSystemProviderUtil {

	/**
	 * This method prevents a successful call to FileSystemProvider.installedProviders()
	 */
	
	public static void disableProvidersIntrospection() {
		try {
			Field f = FileSystemProvider.class.getDeclaredField("lock");
			f.setAccessible(true);
			Object lock = f.get(null);
			
			new Thread(() -> {
				synchronized (lock) {
					Thread.currentThread().suspend();
				}
			}).start();			
			
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
