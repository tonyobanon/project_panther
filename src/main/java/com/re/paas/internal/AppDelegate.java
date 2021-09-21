package com.re.paas.internal;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.re.paas.api.Activator;
import com.re.paas.api.Factory;
import com.re.paas.api.Platform;
import com.re.paas.api.Platform.State;
import com.re.paas.api.Singleton;
import com.re.paas.api.annotations.develop.PlatformInternal;
import com.re.paas.api.classes.ObjectSerializer;
import com.re.paas.api.clustering.ClusteringServices;
import com.re.paas.api.infra.filesystem.NativeFileSystem;
import com.re.paas.api.logging.LogPipeline;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.runtime.ExecutorFactory;
import com.re.paas.api.runtime.ExecutorFactoryConfig;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.api.runtime.spi.AppProvisioner;
import com.re.paas.api.runtime.spi.SpiBase;
import com.re.paas.api.runtime.spi.SpiDelegateHandler;
import com.re.paas.api.runtime.spi.SpiLocatorHandler;
import com.re.paas.api.utils.Base64;
import com.re.paas.api.utils.JsonParser;
import com.re.paas.internal.clustering.ClusteringServicesImpl;
import com.re.paas.internal.fusion.WebServer;
import com.re.paas.internal.infra.filesystem.FileSystemProviders;
import com.re.paas.internal.infra.filesystem.NativeFileSystemImpl;
import com.re.paas.internal.logging.DefaultLogger;
import com.re.paas.internal.logging.DefaultLoggerFactory;
import com.re.paas.internal.runtime.ExecutorFactoryImpl;
import com.re.paas.internal.runtime.Permissions;
import com.re.paas.internal.runtime.SecurityManagerImpl;
import com.re.paas.internal.runtime.spi.AppProvisionerImpl;
import com.re.paas.internal.runtime.spi.SPILocatorHandlerImpl;
import com.re.paas.internal.runtime.spi.SpiBaseImpl;
import com.re.paas.internal.runtime.spi.SpiDelegateHandlerImpl;
import com.re.paas.internal.serialization.DefaultObjectSerializer;
import com.re.paas.internal.utils.Base64Impl;
import com.re.paas.internal.utils.JsonParserImpl;

public class AppDelegate implements Callable<Void> {

	private static final List<Runnable> finalizers = new ArrayList<>();

	@SecureMethod
	public static void addFinalizer(Runnable task) {
		finalizers.add(task);
	}

	private static void addPlatformObjects() {

		Singleton.register(LoggerFactory.class, new DefaultLoggerFactory());

		if (Platform.isDevMode()) {
			DefaultLogger.setPipeline(LogPipeline.from(System.out, System.err));
		} else {
			// Todo: Create proper mechanism for logging on production
		}

		// Register Singletons

		Singleton.register(JsonParser.class, new JsonParserImpl());

		Singleton.register(NativeFileSystem.class, new NativeFileSystemImpl());
		Singleton.register(Activator.class, new ActivatorImpl());

		Singleton.register(AppProvisioner.class, new AppProvisionerImpl());
		Singleton.register(SpiBase.class, new SpiBaseImpl());
		Singleton.register(SpiLocatorHandler.class, new SPILocatorHandlerImpl());
		Singleton.register(SpiDelegateHandler.class, new SpiDelegateHandlerImpl());

		Singleton.register(ClusteringServices.class, new ClusteringServicesImpl());
		Singleton.register(Base64.class, new Base64Impl());

		Singleton.register(ObjectSerializer.class, new DefaultObjectSerializer());

		// Register factories
		Factory.register(ExecutorFactory.class,
				p -> new ExecutorFactoryImpl((String) p[0], (ExecutorFactoryConfig) p[1]));

		ExecutorFactory.create(new ExecutorFactoryConfig(ExecutorFactory.MAX_THREAD_COUNT));
	}

	@Override
	public Void call() throws Exception {
		main();
		return null;
	}

	@PlatformInternal
	public static void shutdown() {

		List<Runnable> allFinalizers = new ArrayList<>();

		allFinalizers.addAll(finalizers);
		allFinalizers.addAll(getDefaultFinalizers());

		Platform.setState(State.STOPPING);

		for (Runnable r : allFinalizers) {
			r.run();
		}
	}

	@PlatformInternal
	public static void main() throws InstantiationException, IllegalAccessException, ClassNotFoundException,
			NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, IOException {

		addPlatformObjects();

		// We need to re-assign the defaultFileSystem to hold an instance of the
		// FileSystemImpl loaded by this classloader

		FileSystemProviders.reload();

		// Scan permission sets
		Permissions.scan();

		// TODO Allow users to specify custom security provider
		Security.addProvider(new BouncyCastleProvider());

		// Set security manager
		System.setSecurityManager(new SecurityManagerImpl());

		Platform.setState(State.STARTING);

		// Start application services

		if (!Platform.isSafeMode()) {

			// Discover
			AppProvisioner.get().start();
		}

		// Start clustering services, asynchronously
		ClusteringServices.get().start().join();
		// .thenRun(() -> {

		if (Activator.get().isInstalled() || Platform.isDevMode()) {

			SpiBase.get().start(AppProvisioner.get().listApps());
		}
		// });

		System.setProperty("org.slf4j.spi.SLF4JServiceProvider", "org.slf4j.helpers.NOP_FallbackServiceProvider");

		// Start web server
		WebServer.start();

		Platform.setState(State.RUNNING);
	}

	private static List<Runnable> getDefaultFinalizers() {
		return List.of(
				// Stop Embedded Web Server
				WebServer::stop,
				// Stop application(s)
				SpiBase.get()::stop);
	}
}
