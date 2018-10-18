package com.re.paas.internal;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Path;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.annotations.PlatformInternal;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.clustering.NodeRegistry;
import com.re.paas.api.clustering.protocol.ClientFactory;
import com.re.paas.api.clustering.protocol.Server;
import com.re.paas.api.designpatterns.Factory;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.networking.AddressResolver;
import com.re.paas.api.spi.SpiBase;
import com.re.paas.api.spi.SpiDelegateHandler;
import com.re.paas.api.spi.SpiLocatorHandler;
import com.re.paas.api.tasks.Scheduler;
import com.re.paas.api.threadsecurity.ThreadSecurity;
import com.re.paas.internal.app_provisioning.AppClassLoaderImpl;
import com.re.paas.internal.app_provisioning.AppProvisioner;
import com.re.paas.internal.app_provisioning.AppProvisionerImpl;
import com.re.paas.internal.clustering.ClusterConfig;
import com.re.paas.internal.clustering.DefaultNodeRegistry;
import com.re.paas.internal.clustering.protocol.ClientFactoryImpl;
import com.re.paas.internal.clustering.protocol.ServerImpl;
import com.re.paas.internal.filesystems.FileSystemProviders;
import com.re.paas.internal.fusion.services.impl.ServerOptions;
import com.re.paas.internal.fusion.services.impl.WebServer;
import com.re.paas.internal.logging.DefaultLoggerFactory;
import com.re.paas.internal.networking.AddressResolverImpl;
import com.re.paas.internal.security.Permissions;
import com.re.paas.internal.security.SecurityManagerImpl;
import com.re.paas.internal.security.ThreadSecurityImpl;
import com.re.paas.internal.spi.SPILocatorHandlerImpl;
import com.re.paas.internal.spi.SpiBaseImpl;
import com.re.paas.internal.spi.SpiDelegateHandlerImpl;

public class AppDelegate {
	
	@BlockerTodo("Move these definitions to a config file")
	private static void addPlatformObjects() {
		
		ThreadSecurity.setInstance(new ThreadSecurityImpl());
		
		// Register Singletons
		
		Singleton.register(AppProvisioner.class, new AppProvisionerImpl());
		Singleton.register(SpiBase.class, new SpiBaseImpl());
		Singleton.register(SpiLocatorHandler.class, new SPILocatorHandlerImpl());
		Singleton.register(SpiDelegateHandler.class, new SpiDelegateHandlerImpl());
		
		Singleton.register(AddressResolver.class, new AddressResolverImpl());
		Singleton.register(NodeRegistry.class, new DefaultNodeRegistry());
		Singleton.register(ClientFactory.class, new ClientFactoryImpl());
		Singleton.register(LoggerFactory.class, new DefaultLoggerFactory());
		
		
		// Register factories
		
		Factory.register(AppClassLoader.class, p -> new AppClassLoaderImpl(AppDelegate.class.getClassLoader(), (Path)p[0], (String)p[1], (String[])p[2]));
		Factory.register(Server.class, p -> new ServerImpl((InetAddress)p[0], (Integer)p[1]));		
	}

	@PlatformInternal
	public static void main() {
		
		FileSystemProviders.init();
		
		addPlatformObjects();
		
		ThreadSecurity.get()
			.setMainThread(Thread.currentThread())
			.trust();
		Permissions.scanPermissions();
		
		System.setSecurityManager(new SecurityManagerImpl());
		
		// TODO Allow users to specify custom security provider
		Security.addProvider(new BouncyCastleProvider());

		if (!Application.IS_SAFE_MODE) {
			
			// Discover application(s)
			AppProvisioner.get().start();
		}

		if(Platform.isInstalled()) {
			
			// Start application(s)
			SpiBase.get().start();
		}
		
		
		System.out.println(AppProvisioner.get().listApps());
		
		// Start Embedded Web Server
		AddressResolver resolver = AddressResolver.get();
		Integer httpPort = ClusterConfig.getInstance().getInteger(ClusterConfig.HTTP_PORT);
		WebServer.start(new ServerOptions().withHost(resolver.httpHost()).withPort(httpPort));
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
		return System.getProperty("java.io.tmpdir") + AppDelegate.getPlatformName() + File.separator +  "internal_config";
	}
}
