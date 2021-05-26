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

	private static void setInternalFs() {

		// We cannot cast <provider> to <FileSystemProviderImpl> because a ClassCastException 
		// will be thrown due to classloader differences. This could have be fixed by setting
		// <FileSystemProviderImpl> as an extrinsicClass in CustomClassLoader, but we can't do
		// so because the class contains some secure methods that needs to be transformed
		// at runtime
		
		FileSystemProvider provider = FileSystems.getDefault().provider();
		

		// At this point, we are sure that provider.getClass() ==
		// com.re.paas.internal.infra.filesystem.FileSystemProviderImpl

		assert provider.getClass() == FileSystemProviderImpl.class;


		// Note: Above (i.e. in the call to FileSystems.getDefault()), when the jvm initialized 
		// FileSystemProviderImpl, it passed in the built in file system into the constructor, 
		// which is then saved in the private static field <provider>
	
		FileSystemProvider iProvider = (FileSystemProvider) ClassUtils.getFieldValue(provider.getClass(), "provider");

		// Save reference to OS-internal file system
		internalFs = iProvider.getFileSystem(URI.create("file:///"));
		
		// System.out.println("Setting internalFs to " + internalFs);
	}

	/**
	 * Reload the Jvm file system. This should be called after meta factory
	 * scanning, because we need to update the default file system class with the
	 * transformed version that contains an indirection to {@link MethodInterceptor}
	 * (if any exists)
	 */
	@SecureMethod
	public static void reload() {

		// At this point, we are sure that FileSystems.getDefault().getClass() ==
		// com.re.paas.internal.infra.filesystem.FileSystemImpl

		assert FileSystems.getDefault().getClass() == FileSystemWrapper.class;

		FileSystemWrapper wrapper = (FileSystemWrapper) FileSystems.getDefault();
		
		FileSystem impl = wrapper.getFileSystem();

		FileSystem fs = (FileSystem) ClassUtils.getFieldValue(impl.getClass(), impl, "fs");

		wrapper.setFileSystem(new FileSystemProviderImpl(fs).getFileSystem(URI.create("file:///")));
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

	static {
		setInternalFs();
	}
	
}
