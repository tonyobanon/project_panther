package com.re.paas.internal;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.security.Security;
import java.util.concurrent.Callable;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.re.paas.api.annotations.develop.PlatformInternal;
import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.clustering.NodeRegistry;
import com.re.paas.api.clustering.protocol.ClientFactory;
import com.re.paas.api.clustering.protocol.Server;
import com.re.paas.api.designpatterns.Factory;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.logging.LogPipeline;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.networking.AddressResolver;
import com.re.paas.api.runtime.ClassLoaderSecurity;
import com.re.paas.api.runtime.ExecutorFactory;
import com.re.paas.api.runtime.ExecutorFactoryConfig;
import com.re.paas.api.runtime.spi.SpiBase;
import com.re.paas.api.runtime.spi.SpiDelegateHandler;
import com.re.paas.api.runtime.spi.SpiLocatorHandler;
import com.re.paas.api.utils.Base64;
import com.re.paas.api.utils.JsonParser;
import com.re.paas.internal.clustering.DefaultNodeRegistry;
import com.re.paas.internal.clustering.protocol.ClientFactoryImpl;
import com.re.paas.internal.clustering.protocol.ServerImpl;
import com.re.paas.internal.compute.Scheduler;
import com.re.paas.internal.fusion.services.impl.WebServer;
import com.re.paas.internal.infra.filesystem.FileSystemProviders;
import com.re.paas.internal.logging.DefaultLogger;
import com.re.paas.internal.logging.DefaultLoggerFactory;
import com.re.paas.internal.networking.AddressResolverImpl;
import com.re.paas.internal.runtime.ExecutorFactoryImpl;
import com.re.paas.internal.runtime.security.ClassLoaderSecurityImpl;
import com.re.paas.internal.runtime.security.Permissions;
import com.re.paas.internal.runtime.security.SecurityManagerImpl;
import com.re.paas.internal.runtime.spi.AppClassLoaderImpl;
import com.re.paas.internal.runtime.spi.AppProvisioner;
import com.re.paas.internal.runtime.spi.AppProvisionerImpl;
import com.re.paas.internal.runtime.spi.SPILocatorHandlerImpl;
import com.re.paas.internal.runtime.spi.SpiBaseImpl;
import com.re.paas.internal.runtime.spi.SpiDelegateHandlerImpl;
import com.re.paas.internal.utils.Base64Impl;
import com.re.paas.internal.utils.JsonParserImpl;

public class AppDelegate implements Callable<Void> {

	private static void addPlatformObjects() {

		FileSystemProviders.init();
		ClassLoaderSecurity.setInstance(new ClassLoaderSecurityImpl());

		// Register Singletons

		Singleton.register(LoggerFactory.class, new DefaultLoggerFactory());

		if (Platform.isDevMode()) {
			DefaultLogger.setPipeline(LogPipeline.from(System.out, System.err));
		} else {
			// Todo: Create proper mechanism for logging on production
		}

		Singleton.register(AppProvisioner.class, new AppProvisionerImpl());
		Singleton.register(SpiBase.class, new SpiBaseImpl());
		Singleton.register(SpiLocatorHandler.class, new SPILocatorHandlerImpl());
		Singleton.register(SpiDelegateHandler.class, new SpiDelegateHandlerImpl());

		Singleton.register(AddressResolver.class, new AddressResolverImpl());
		Singleton.register(NodeRegistry.class, new DefaultNodeRegistry());
		Singleton.register(ClientFactory.class, new ClientFactoryImpl());
		Singleton.register(JsonParser.class, new JsonParserImpl());
		Singleton.register(Base64.class, new Base64Impl());

		// Register factories

		Factory.register(AppClassLoader.class,
				p -> new AppClassLoaderImpl(ClassLoader.getSystemClassLoader(), (String) p[0], (String[]) p[1]));
		Factory.register(Server.class, p -> new ServerImpl((InetAddress) p[0], (Integer) p[1]));

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
	public static void main() throws InstantiationException, IllegalAccessException, ClassNotFoundException,
			NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {

		// Register platform objects, including singletons and factories
		addPlatformObjects();

		// Reload file system
		FileSystemProviders.reload();

		// Scan permission sets
		Permissions.scan();

		// TODO Allow users to specify custom security provider
		Security.addProvider(new BouncyCastleProvider());

		// Add Jvm shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			stop();
		}));

		// Set security manager
		System.setSecurityManager(new SecurityManagerImpl());

		if (!Platform.isSafeMode()) {

			// Discover application(s)
			AppProvisioner.get().start();
		}

		if (Platform.isInstalled() || Platform.isDevMode()) {

			// Start application(s)
			SpiBase.get().start(AppProvisioner.get().listApps());
		}
//
//		System.out.println(AppProvisioner.get().listApps());
//
//		// Start Embedded Web Server
//		CloudEnvironment env = CloudEnvironment.get();
//		WebServer.start(
//				new ServerOptions().withHost(env.httpHost()).withPort(env.httpPort()).withSslPort(env.httpsPort()));
	}

	@PlatformInternal
	public static void stop() {

		// Stop Embedded Web Server
		WebServer.stop();

		// Stop application(s)
		SpiBase.get().stop();

		// Shutdown default executor
		Scheduler.getDefaultExecutor().shutdownNow();
	}

	public static String getPlatformName() {
		return "CE Enterprise Suite";
	}

	public static String BaseConfigDir() {
		return System.getProperty("java.io.tmpdir") + AppDelegate.getPlatformName() + File.separator
				+ "internal_config";
	}
}
