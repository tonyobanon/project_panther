package com.re.paas.internal.runtime.spi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.re.paas.api.annotations.develop.BlockerBlockerTodo;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.runtime.ClassLoaders;
import com.re.paas.api.runtime.ParameterizedInvokable;
import com.re.paas.api.runtime.RuntimeIdentity;
import com.re.paas.internal.classes.ClassUtil;
import com.re.paas.internal.fusion.services.RoutingContextHandler;

public class AppClassLoaderImpl extends AppClassLoader {

	private static List<String> appIntrinsicClasses = new ArrayList<>();
	private static Logger LOG = LoggerFactory.get().getLog(AppClassLoaderImpl.class);

	private final Map<String, Class<?>> thirdPartyClasses = Maps.newHashMap();

	private final String appId;
	private final Path path;
	private boolean isStopping = false;

	@BlockerTodo("For high performance, I advise that we stop using using appIntrinsicClasses, "
			+ "but use a standard naming convention in the class names")
	public static boolean isAppIntrinsic(String classname) {
		return appIntrinsicClasses.contains(classname);
	}

	public AppClassLoaderImpl(ClassLoader parent, String appId) {
		super(parent);
		this.appId = appId;
		this.path = AppProvisionerImpl.getAppBasePath().resolve(appId);
	}

	public URL getResource(String name) {
		URL url = this.findResource(name);
		if (url == null) {
			url = super.getResource(name);
		}
		return url;
	}

	@Override
	protected URL findResource(String name) {
		try {
			return path.resolve("resources").resolve(name).toUri().toURL();
		} catch (MalformedURLException e) {
			return (URL) Exceptions.throwRuntime(e);
		}
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		return findClass0(name, isAppIntrinsic(name));
	}

	public Class<?> findClass0(String name, boolean isAppIntrinsic) throws ClassNotFoundException {
		
		byte[] data = loadClassFromDisk(name, isAppIntrinsic);

		if (data == null) {
			throw new ClassNotFoundException(name);
		}

		SecurityManager sm = System.getSecurityManager();

		if (sm != null) {
			String pkg = ClassUtil.getPackageName(name);
			sm.checkPackageDefinition(pkg);
		}

		return this.defineClass(name, data, 0, data.length, null);
	}

	private Class<?> loadClass0(String name, boolean isAppIntrinsic) throws ClassNotFoundException {

		Class<?> c = null;
		try {

			// Find class
			c = findClass0(name, isAppIntrinsic);

		} catch (ClassNotFoundException e) {

			// Search in all available app classloaders

			ObjectWrapper<Class<?>> cWrapper = new ObjectWrapper<>();

			AppProvisionerImpl.appClassloaders.entrySet().forEach(entry -> {

				if (cWrapper.get() != null) {
					return;
				}

				try {
					cWrapper.set(((AppClassLoaderImpl) entry.getValue()).findClass0(name, false));
				} catch (ClassNotFoundException ex) {
				}

			});

			if (cWrapper.get() != null) {
				
				c = cWrapper.get();

				thirdPartyClasses.put(name, c);
				SPILocatorHandlerImpl.addDependencyPath0(getAppId(), Arrays.asList(ClassLoaders.getId(cWrapper.get())));
			}
		}

		
		if(c == null) {
			throw new ClassNotFoundException(name);
		}
		
		return c;
	}

	@BlockerTodo("Using checkPackageAccess(..), we could orchestrate scenarios where an app blocks other app(s) from depending on it")
	private Class<?> load0(String name) throws ClassNotFoundException {

		Class<?> c = null;
		boolean isAppIntrinsic = isAppIntrinsic(name);

		List<ClassLoader> classloaders = Arrays.asList(ClassLoaders.getClassLoader(), this);

		if (isAppIntrinsic) {
			classloaders.add(classloaders.remove(0));
		}

		for (ClassLoader cl : classloaders) {
			try {
				c = cl instanceof AppClassLoader ? ((AppClassLoaderImpl) cl).loadClass0(name, isAppIntrinsic)
						: cl.loadClass(name);
			} catch (ClassNotFoundException e) {
			}

			if (c != null) {

				// If the classes was loaded from the system class loader
				if (c.getClassLoader() == ClassLoaders.getClassLoader()) {

					SecurityManager sm = System.getSecurityManager();

					// Here, we are using the className instead of pkg, because we also want to
					// check for classes as well
					sm.checkPackageAccess(name);
				}
				break;
			}
		}

		return c;
	}

	@Override
	public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

		Class<?> c = findLoadedClass(name);

		if (c != null) {
			return c;
		}

		if (!isAppIntrinsic(name)) {

			c = super.findLoadedClass(name);

			if (c != null) {
				return c;
			}
		}

		c = thirdPartyClasses.get(name);

		if (c != null) {
			return c;
		}

		synchronized (getClassLoadingLock(name)) {
			c = load0(name);
		}

		if (c != null) {
			return c;
		}

		throw new ClassNotFoundException(name);
	}
	
	private final Path getClassPath(String className, Path path) {
		return path.resolve(className.replace(".", File.separator) + ".class");
	}

	@BlockerBlockerTodo("Add support for nested classes, as this may not work in that scenario")
	private byte[] loadClassFromDisk(String className, boolean appIntrinsic) {

		List<Path> paths = new ArrayList<>(2);

		if (appIntrinsic) {
			paths.add(getClassPath(className, Classpaths.get()));
		}

		paths.add(getClassPath(className, this.path.resolve("classes")));

		for (Path path : paths) {

			if (Files.exists(path)) {

				try {

					// read class
					InputStream in = Files.newInputStream(path);
					ByteArrayOutputStream byteSt = new ByteArrayOutputStream();

					// write into byte
					int len = 0;
					while ((len = in.read()) != -1) {
						byteSt.write(len);
					}

					in.close();

					// convert into byte array
					return byteSt.toByteArray();

				} catch (IOException e) {
					Exceptions.throwRuntime(e);
					return null;
				}
			}
		}

		return null;
	}

	public Path getPath() {
		return path;
	}

	public String getAppId() {
		return appId;
	}

	public boolean isStopping() {
		return isStopping;
	}

	public void setStopping(boolean isStopping) {
		this.isStopping = isStopping;
	}

	static {

		registerAsParallelCapable();

		appIntrinsicClasses.add(RuntimeIdentity.class.getName());
		appIntrinsicClasses.add(ParameterizedInvokable.class.getName());
		appIntrinsicClasses.add(RoutingContextHandler.class.getName());
	}
}
