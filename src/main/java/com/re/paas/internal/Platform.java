package com.re.paas.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.internal.infra.filesystem.FileSystemProviders;

public class Platform {

	private static Boolean isInstalled = false;

	private static final String API_PACKAGE = "com.re.paas.api.";
	private static final String INTERNAL_PACKAGE = "com.re.paas.internal.";

	public static boolean isProduction() {
		String env = System.getenv("REALIGNITE_ENVIRONMENT");
		return env == null || !env.equals("development");
	}

	public static Boolean isInstalled() {
		return isInstalled;
	}

	public static Path getResourcePath() {
		return FileSystemProviders.getInternal().getPath("/opt/" + getPlatformPrefix() + "/resources");
	}

	public static String getPlatformPrefix() {
		return "realignite";
	}

	public static String getNodePrefix() {
		return getPlatformPrefix() + "-node";
	}

	public static String getPlatformName() {
		return "Real Ignite SaaS Solution";
	}

	public static Path getBaseDir() {
		return Paths.get("/", getPlatformPrefix());
	}

	public static String[] getAccessForbiddenClasses() {
		return new String[] {
				// Reason: This class uses the platform's local file system. As an alternative, the developer should use 
				// FileSystemProvider. Also for file creation, newByteChannel(..) should be used
				"java.io.File" };
	}

	public static String[] getAccessForbiddenPackages() {
		return new String[] { "sun.", "com.sun.", "jdk.internal" };
	}

	public static String[] getDefineForbiddenPackages() {
		return new String[] { "java.", "javax.", "jdk.", API_PACKAGE, INTERNAL_PACKAGE };
	}

	static {

		// Set up resource path
		if (!Files.exists(getResourcePath())) {
			try {
				Files.createDirectories(getResourcePath());
			} catch (IOException e) {
				Exceptions.throwRuntime(e);
			}
			isInstalled = false;
		} else {
			isInstalled = true;
		}
	}

}
