package com.re.paas.internal.runtime.spi;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.re.paas.api.Activator;
import com.re.paas.api.Factory;
import com.re.paas.api.Platform;
import com.re.paas.api.Platform.State;
import com.re.paas.api.Singleton;
import com.re.paas.api.annotations.develop.PlatformInternal;
import com.re.paas.api.classes.ObjectSerializer;
import com.re.paas.api.clustering.ClusteringServices;
import com.re.paas.api.fusion.MimeTypeDetector;
import com.re.paas.api.fusion.components.WebClientConnector;
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
import com.re.paas.internal.fusion.MimeTypeDetectorImpl;
import com.re.paas.internal.fusion.WebSocketServer;
import com.re.paas.internal.fusion.components.WebClientConnectorImpl;
import com.re.paas.internal.fusion.HttpServer;
import com.re.paas.internal.infra.filesystem.FileSystemProviders;
import com.re.paas.internal.infra.filesystem.NativeFileSystemImpl;
import com.re.paas.internal.logging.DefaultLogger;
import com.re.paas.internal.logging.DefaultLoggerFactory;
import com.re.paas.internal.runtime.ExecutorFactoryImpl;
import com.re.paas.internal.runtime.Permissions;
import com.re.paas.internal.runtime.RuntimeIdentityImpl;
import com.re.paas.internal.runtime.SecurityManagerImpl;
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

		RuntimeIdentityImpl.noOp();

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
		Singleton.register(MimeTypeDetector.class, new MimeTypeDetectorImpl());
		
		// Todo: Make ComponentsDelegate an actual Delegate, instead of a Singleton
		Singleton.register(WebClientConnector.class, new WebClientConnectorImpl());

		
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
		
		ForkJoinPool.commonPool().awaitQuiescence(30, TimeUnit.SECONDS);
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

		// Start clustering services, asynchronously
		ClusteringServices.get().start().join();

		// Start apps
		SpiBaseImpl.start();

		if (Activator.get().isInstalled()) {
			// Start platform services
			AppProvisioner.get().scanApps();

		}

		FusionClassloaders.loadFusionClient();
		
		// Start http server
		HttpServer.start();
		
		// Start web socket server
		WebSocketServer.start();

		Platform.setState(State.RUNNING);
	}

	private static List<Runnable> getDefaultFinalizers() {
		return List.of(
				// Stop Embedded Servers
				HttpServer::stop,
				WebSocketServer::stop,
				// Stop application services
				SpiBaseImpl::stop);
	}
}
