package com.re.paas.internal.runtime.spi;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.re.paas.api.Platform;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.runtime.spi.AppProvisioner;
import com.re.paas.internal.infra.filesystem.FileSystemProviders;
import com.re.paas.internal.utils.FileUtils;

public class FusionClassloaders {

	private static final Map<String, ClassLoader> fusionClassloaders = new HashMap<>();

	static void init() {

		Path fusionClient = getPlatformFusionClient();

		if (fusionClient != null) {
			addFusionClient(AppProvisioner.DEFAULT_APP_ID, fusionClient);
		}
	}

	static void addFusionClient(String appId, Path jarFile) {

		updateFusionClient(appId, jarFile);

		if (Platform.isDevMode()) {

			new Thread(() -> {

				try {
					WatchService watchService = FileSystemProviders.getInternal().newWatchService();

					jarFile.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

					WatchKey key;
					while ((key = watchService.take()) != null) {
						for (WatchEvent<?> event : key.pollEvents()) {

							if (event.context().toString().equals(jarFile.getFileName().toString())) {

								updateFusionClient(appId, jarFile);
							}
						}
						key.reset();
					}

				} catch (IOException | InterruptedException e) {
					Exceptions.throwRuntime(e);
				}

			}).start();
		}
	}

	private static Path refreshPath(Path p) {
		try {

			if (Files.exists(p)) {
				FileUtils.deleteDirectory(p);
			}

			Files.createDirectories(p);

			return p;

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	private static void write(ZipInputStream zip, Path p) {

		try {
			Files.createDirectories(p.getParent());

			Files.createFile(p);

			zip.transferTo(Files.newOutputStream(p));

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	private static void updateFusionClient(String appId, Path jarFile) {

		// Extract classes and static assets

		Path staticDir = refreshPath(getFusionStaticPath().resolve(appId));
		Path classesDir = refreshPath(getFusionClassesPath().resolve(appId));

		try (var fileIn = Files.newInputStream(jarFile)) {

			ZipInputStream zip = new ZipInputStream(fileIn);
			for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {

				if (entry.isDirectory() || entry.getName().startsWith("META-INF/")) {
					continue;
				}

				Path p = null;

				if (entry.getName().startsWith("resources/")) {
					p = staticDir.resolve(entry.getName().replaceFirst("resources/", ""));
				} else {

					String pkgName = Platform.getComponentBasePkg() + "." + appId;

					if (!entry.getName().startsWith(pkgName.replace('.', File.separatorChar))) {

						Exceptions.throwRuntime(
								"Unknown class file: " + entry.getName() + " in " + Platform.getFusionClientJarname());
					}

					p = classesDir.resolve(entry.getName());
				}

				write(zip, p);
			}
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

		CustomClassLoader cl = new CustomClassLoader(false).disableForwarding().setPath(classesDir.toUri());

		fusionClassloaders.put(appId, cl);
	}

	private static Path getPlatformFusionClient() {

		FileSystem fs = FileSystemProviders.getInternal();

		String cp = ManagementFactory.getRuntimeMXBean().getClassPath();

		for (String e : cp.split(":")) {

			String[] arr = e.split(File.separator);
			String jarName = arr[arr.length - 1];

			if (jarName.equals(Platform.getFusionClientJarname())) {
				return fs.getPath(e);
			}
		}

		return null;
	}

	private static Path getFusionStaticPath() {
		return FileSystemProviders.getResourcePath().resolve("fusion").resolve("static");
	}

	private static Path getFusionClassesPath() {
		return FileSystemProviders.getResourcePath().resolve("fusion").resolve("classes");
	}

	static Class<?> loadComponentClass(String name) throws ClassNotFoundException {

		String appId = getAppIdFromClassName(name);
		ClassLoader cl = fusionClassloaders.get(appId);

		if (cl == null) {
			throw new RuntimeException("Unknown appId: " + appId);
		}

		assert cl != null;

		return cl.loadClass(name);
	}

	private static String getAppIdFromClassName(String name) {

		String appId = null;

		name = name.replace(Platform.getComponentBasePkg() + ".", "");

		String[] arr = name.split("\\Q.\\E");

		if (arr.length == 3) {
			// appId, assetId, className
			appId = arr[0];
		}

		if (appId == null) {
			throw new RuntimeException("Unknown class: " + name);
		}

		return appId;
	}

}