package com.re.paas.internal;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
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
import com.re.paas.api.clustering.protocol.ClientFactory;
import com.re.paas.api.clustering.protocol.Server;
import com.re.paas.api.infra.filesystem.NativeFileSystem;
import com.re.paas.api.logging.LogPipeline;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.networking.InetAddressResolver;
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
import com.re.paas.internal.clustering.protocol.ClientFactoryImpl;
import com.re.paas.internal.clustering.protocol.ServerImpl;
import com.re.paas.internal.compute.Scheduler;
import com.re.paas.internal.fusion.WebServer;
import com.re.paas.internal.infra.filesystem.FileSystemProviders;
import com.re.paas.internal.infra.filesystem.NativeFileSystemImpl;
import com.re.paas.internal.logging.DefaultLogger;
import com.re.paas.internal.logging.DefaultLoggerFactory;
import com.re.paas.internal.networking.InetAddressResolverImpl;
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

		Singleton.register(InetAddressResolver.class, new InetAddressResolverImpl());
		Singleton.register(ClusteringServices.class, new ClusteringServicesImpl());
		Singleton.register(ClientFactory.class, new ClientFactoryImpl());
		Singleton.register(Base64.class, new Base64Impl());

		Singleton.register(ObjectSerializer.class, new DefaultObjectSerializer());
		

		// Register factories

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
			NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, IOException {

		addPlatformObjects();


		// We need to re-assign the defaultFileSystem to hold an instance of the 
		// FileSystemImpl loaded by this classloader
		
		FileSystemProviders.reload();

		// Scan permission sets
		Permissions.scan();

		// TODO Allow users to specify custom security provider
		Security.addProvider(new BouncyCastleProvider());
		
		List<Runnable> allFinalizers = new ArrayList<>();
		
		allFinalizers.addAll(finalizers);
		allFinalizers.addAll(getDefaultFinalizers());

		// Add Jvm shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			
			Platform.setState(State.STOPPING);
			
			for (Runnable r : allFinalizers) {
				r.run();
			}
		}));

		// Set security manager
		System.setSecurityManager(new SecurityManagerImpl());


		Platform.setState(State.STARTING);

		// Start application services

		if (!Platform.isSafeMode()) {

			// Discover
			AppProvisioner.get().start();
		}
		

		// Start clustering services
		// ClusteringServices.get().start();


		if (Activator.get().isInstalled() || Platform.isDevMode()) {

			// Boot up
			SpiBase.get().start(AppProvisioner.get().listApps());
		}

		// System.out.println(AppProvisioner.get().listApps());

		// Start web server
		WebServer.start();

		// Set member status to online
//		Member member = ClusteringServices.get().getMember().setStatus(MemberStatus.ONLINE);
//
//		Function.execute(ClusterDestination.ALL_NODES, ASYNC_DISPATCH_EVENT,
//				new MemberStateChangeEvent().setMemberId(member.getMemberId()).setNewState(member.getStatus()));


		Platform.setState(State.RUNNING);
	}

	private static List<Runnable> getDefaultFinalizers() {
		return List.of(
				// Stop Embedded Web Server
				WebServer::stop,
				// Stop application(s)
				SpiBase.get()::stop,
				// Shutdown default executor
				Scheduler.getDefaultExecutor()::shutdownNow);
	}
}
