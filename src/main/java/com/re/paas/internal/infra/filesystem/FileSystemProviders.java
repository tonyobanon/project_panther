package com.re.paas.internal.infra.filesystem;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.spi.FileSystemProvider;

import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.Application;

public class FileSystemProviders {

	private static FileSystem internalFs;

	/**
	 * This function eagerly sets the Jvm FileSystemProvider on app startup, since
	 * this cannot be changed later in time
	 */
	public static void init() {
		System.setProperty("java.nio.file.spi.DefaultFileSystemProvider", FileSystemProviderImpl.class.getName());
		initInternalFs();
	}

	@SuppressWarnings("unchecked")
	private static void initInternalFs() {

		ClassLoader cl = Application.class.getClassLoader();
		Class<FileSystemProvider> internalFsProviderClass = null;
		FileSystemProvider internalFsProvider = null;

		String os = System.getProperty("os.name").toLowerCase();

		try {
			if (os.indexOf("win") >= 0) {
				internalFsProviderClass = (Class<FileSystemProvider>) cl
						.loadClass("sun.nio.fs.WindowsFileSystemProvider");
			} else if (os.indexOf("mac") >= 0) {
				internalFsProviderClass = (Class<FileSystemProvider>) cl.loadClass("sun.nio.fs.MacOSXFileSystemProvider");
			} else if (os.indexOf("sunos") >= 0) {
				internalFsProviderClass = (Class<FileSystemProvider>) cl
						.loadClass("sun.nio.fs.SolarisFileSystemProvider");
			} else if (os.indexOf("unix") >= 0) {
				internalFsProviderClass = (Class<FileSystemProvider>) cl.loadClass("sun.nio.fs.UnixFileSystemProvider");
			} else if (os.indexOf("linux") >= 0) {
				internalFsProviderClass = (Class<FileSystemProvider>) cl
						.loadClass("sun.nio.fs.LinuxFileSystemProvider");
			} else if (os.indexOf("aix") >= 0) {
				internalFsProviderClass = (Class<FileSystemProvider>) cl.loadClass("sun.nio.fs.AixFileSystemProvider");
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		internalFsProvider = ClassUtils.createInstance(internalFsProviderClass);
		internalFs = internalFsProvider.getFileSystem(URI.create("file:///"));
	}

	/**
	 * This returns the underlying file system used by the operating system
	 * 
	 * @return
	 */
	public static FileSystem getInternal() {
		return internalFs;
	}
}
