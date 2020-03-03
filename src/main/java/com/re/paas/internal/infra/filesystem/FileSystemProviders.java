package com.re.paas.internal.infra.filesystem;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

import com.re.paas.api.Platform;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.runtime.MethodInterceptor;

public class FileSystemProviders {

	private static FileSystem internalFs;

	@SecureMethod
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
	 * transformed version that contains an indirection to {@link MethodInterceptor} (if
	 * any exists)
	 */
	@SecureMethod
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
	@SecureMethod
	public static FileSystem getInternal() {
		return internalFs;
	}

	@SecureMethod
	public static Path getResourcePath() {
		try {
	
			Path basePath = getInternal().getPath(System.getProperty("user.home"));
			Path p = basePath.resolve(Platform.getPlatformPrefix()).resolve("resources");
	
			Files.createDirectories(p);
	
			return p;
	
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}
	
	
}
