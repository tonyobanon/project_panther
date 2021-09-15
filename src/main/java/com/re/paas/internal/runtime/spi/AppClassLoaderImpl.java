package com.re.paas.internal.runtime.spi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.re.paas.api.Platform;
import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.runtime.ClassLoaders;
import com.re.paas.api.runtime.Invokable;
import com.re.paas.api.runtime.ParameterizedInvokable;
import com.re.paas.api.runtime.RuntimeIdentity;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.classes.ClassUtil;
import com.re.paas.internal.fusion.RoutingContextHandler;

public class AppClassLoaderImpl extends AppClassLoader {

	private static List<String> intrinsicClasses = new ArrayList<>();

	private final String appId;
	private final Path path;
	private boolean isStopping = false;

	private final URLClassLoader libCl;

	public static boolean isIntrinsic(String classname) {
		return intrinsicClasses.contains(classname);
	}

	AppClassLoaderImpl(ClassLoader parent, String appId) {
		super(parent);

		this.appId = appId;
		this.path = AppProvisionerImpl.getAppBasePath().resolve(appId);

		this.libCl = createLibClassLoader();
	}

	private URLClassLoader createLibClassLoader() {

		Path libFolder = this.path.resolve("lib");
		List<URL> jars = new ArrayList<>();

		try {

			Files.list(libFolder).forEach(f -> {

				if (!f.endsWith(".jar")) {
					// Skip, we do not know how to handle this file
					return;
				}

				if (f.getFileName().toString().equals(Platform.getFusionClientJarname())) {

					FusionClassloaders.addFusionClient(appId, f);

					// Note: all components are loaded in a designated class loader defined
					// by our system classloader, hence we do not add this jar to <libCl> so that
					// class lookups can be delegated to the system classloader

				} else {

					try {
						jars.add(new URL("jar:file:" + f.toString()));
					} catch (MalformedURLException e) {
						Exceptions.throwRuntime(e);
					}
				}
			});

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

		return new URLClassLoader(jars.toArray(new URL[jars.size()]), null);
	}

	@Override
	public URLClassLoader getLibraryClassLoader() {
		return libCl;
	}

	@Todo
	public URL getResource(String name) {
		return this.findResource(name);
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
		return findClass0(name, isIntrinsic(name));
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

	private Class<?> load0(String name) throws ClassNotFoundException {

		Class<?> c = null;
		boolean isAppIntrinsic = isIntrinsic(name);

		List<ClassLoader> classloaders = Arrays.asList(ClassLoaders.getClassLoader(), this);

		if (isAppIntrinsic) {
			classloaders.add(classloaders.remove(0));
		}

		for (ClassLoader cl : classloaders) {
			try {
				c = cl instanceof AppClassLoader ? ((AppClassLoaderImpl) cl).findClass0(name, isAppIntrinsic)
						: cl.loadClass(name);
			} catch (ClassNotFoundException e) {
			}

			if (c != null) {

				SystemClassLoaderImpl scl = (SystemClassLoaderImpl) ClassLoaders.getClassLoader();

				// If the classes was loaded from the system class loader
				if (c.getClassLoader() == scl.getClassloader()) {

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

		if (!isIntrinsic(name)) {

			c = super.findLoadedClass(name);

			if (c != null) {
				return c;
			}
		}

		c = libCl.loadClass(name);

		if (c != null) {
			return c;
		}

		synchronized (getClassLoadingLock(name)) {
			return load0(name);
		}
	}

	private final Path getClassPath(String className, Path path) {
		return path.resolve(className.replace(".", File.separator) + ".class");
	}

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

		intrinsicClasses.add(ClassUtils.getName(RuntimeIdentity.class));
		intrinsicClasses.add(ClassUtils.getName(ParameterizedInvokable.class));
		intrinsicClasses.add(ClassUtils.getName(Invokable.class));
		intrinsicClasses.add(ClassUtils.getName(RoutingContextHandler.class));
	}
}
