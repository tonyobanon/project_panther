package com.re.paas.internal.infra.filesystem;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.spi.FileSystemProvider;

import com.re.paas.api.annotations.ProtectionContext;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.Interceptor;

public class FileSystemProviders {

	private static FileSystem internalFs;

	@ProtectionContext
	public static void init() {
		System.setProperty("java.nio.file.spi.DefaultFileSystemProvider", FileSystemProviderImpl.class.getName());

		FileSystemProvider provider = FileSystems.getDefault().provider();

		// At this point, we are sure that provider.getClass() ==
		// com.re.paas.internal.infra.filesystem.FileSystemProviderImpl

		// However, since this method is also called in AppDelegate, we cannot
		// cast normally, because the active classloader would have changed then, hence
		// a ClassCastException will be thrown. Hence, the need to use reflection

		FileSystemProvider iProvider = (FileSystemProvider) ClassUtils.getFieldValue(provider.getClass(), "provider");

		// Save reference to OS-internal file system
		internalFs = iProvider.getFileSystem(URI.create("file:///"));

		System.clearProperty("java.nio.file.spi.DefaultFileSystemProvider");
	}

	/**
	 * Reload the Jvm file system. This should be called after scanning protection
	 * context, because we need to update the default file system class with the
	 * transformed version that contains an indirection to {@link Interceptor} (if
	 * any exists)
	 */
	@ProtectionContext
	public static void reload() {

		// At this point, we are sure that FileSystems.getDefault().getClass() ==
		// com.re.paas.internal.infra.filesystem.FileSystemImpl
		
		FileSystem fs0 = FileSystems.getDefault();

		FileSystem fs = (FileSystem) ClassUtils.getFieldValue(fs0.getClass(), fs0, "fs");
		
		ClassUtils.updateField(FileSystems.class.getDeclaredClasses()[0], "defaultFileSystem",
				new FileSystemProviderImpl(fs).getFileSystem(URI.create("file:///")));
	}

	/**
	 * This returns the underlying file system used by the operating system
	 * 
	 * @return
	 */
	@ProtectionContext
	public static FileSystem getInternal() {
		return internalFs;
	}
}
