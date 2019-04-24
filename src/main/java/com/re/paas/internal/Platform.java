package com.re.paas.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.infra.filesystem.FileSystemProviders;

public class Platform {

	private static Boolean isInstalled = false;

	private static final String API_PACKAGE = "com.re.paas.api.";
	private static final String INTERNAL_PACKAGE = "com.re.paas.internal.";

	private static boolean IS_SAFE_MODE;
	private static boolean IS_ADVANCED_MODE;

	public static void readFlags(String[] args) {
		Map<String, Boolean> jvmFlags = Utils.getFlags(args);

		if (jvmFlags.get("safemode") != null && jvmFlags.get("safemode").booleanValue() == true) {
			IS_SAFE_MODE = true;
		}

		if (jvmFlags.get("advancedmode") != null && jvmFlags.get("advancedmode").booleanValue() == true) {
			IS_ADVANCED_MODE = true;
		}

	}

	public static boolean isSafeMode() {
		return IS_SAFE_MODE;
	}

	public static boolean isAdvancedMode() {
		return IS_ADVANCED_MODE;
	}

	public static boolean isProduction() {
		String env = System.getenv("REALIGNITE_ENVIRONMENT");
		return env == null || !env.equals("development");
	}

	@BlockerTodo("Write logic to determine if Platform is installed")
	public static Boolean isInstalled() {
		return isInstalled;
	}

	public static Path getResourcePath() {
		try {

			Path basePath = FileSystemProviders.getInternal().getPath(System.getProperty("user.home"));
			Path p = basePath.resolve(getPlatformPrefix()).resolve("resources");

			Files.createDirectories(p);

			return p;

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
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
				// Reason: This class uses the platform's local file system. As an alternative,
				// the developer should use FileSystemProvider. Also for file creation,
				// newByteChannel(..) should be used
				"java.io.File" };
	}

	public static String[] getAccessForbiddenPackages() {
		return new String[] { "sun.", "com.sun.", "jdk.internal" };
	}

	public static String[] getDefineForbiddenPackages() {
		return new String[] { "java.", "javax.", "jdk.", API_PACKAGE, INTERNAL_PACKAGE };
	}

}
