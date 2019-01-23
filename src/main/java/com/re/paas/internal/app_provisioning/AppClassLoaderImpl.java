package com.re.paas.internal.app_provisioning;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.google.common.collect.Maps;
import com.re.paas.api.annotations.BlockerBlockerTodo;
import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.annotations.JdkUpgradeTask;
import com.re.paas.api.annotations.Prototype;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.utils.ClassUtils;

/**
 * <b>Internal Implementation guidelines:</b>
 * <li>...</li>
 * 
 * @author Tony
 */
@Prototype
public class AppClassLoaderImpl extends AppClassLoader {

	private final Map<String, Class<?>> loadedClasses = Maps.newHashMap();

	private final String appId;
	private final Path path;
	private boolean isStopping = false;

	private final String[] appDependencies;

	public AppClassLoaderImpl(ClassLoader parent, Path path, String appId) {
		this(parent, path, appId, null);
	}

	public AppClassLoaderImpl(ClassLoader parent, Path path, String appId, String[] appDependencies) {
		super(parent);
		this.appId = appId;
		this.path = path;
		this.appDependencies = appDependencies;
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
			return path.resolve(name).toUri().toURL();
		} catch (MalformedURLException e) {
			return (URL) Exceptions.throwRuntime(e);
		}
	}

	private Class<?> loadClassDelegateFirst(String name) {

		Class<?> c = null;

		// First, Delegate to parent classloader
		try {
			c = super.loadClass(name, false);
			return c;

		} catch (ClassNotFoundException e) {
		}

		// Then, check if the class has already been loaded by the current classloader
		c = findLoadedClass(name);

		if (c != null) {
			return c;
		}

		return c;
	}

	private Class<?> loadClassDelegateLater(String name) {

		Class<?> c = null;

		// First, check if the class has already been loaded by the current classloader
		c = findLoadedClass(name);

		if (c != null) {
			return c;
		}

		// Then, Delegate to parent classloader
		try {
			c = super.loadClass(name, false);
			return c;

		} catch (ClassNotFoundException e) {
		}

		return c;
	}

	@JdkUpgradeTask("Verify that Jdk9 still throws a ClassNotFoundException, which is almost useless")
	@BlockerTodo("Implement for other delegation types")

	@Override
	public Class<?> loadClass(String name, boolean resolve) {

		String pkg = ClassUtils.getPackageName(name);
		SecurityManager sm = System.getSecurityManager();

		if (sm != null) {
			// Here, we are using the className instead of pkg, because we also want to
			// check for classes as well
			sm.checkPackageAccess(name);
		}

		synchronized (getClassLoadingLock(name)) {

			Class<?> c = null;

			switch (getDelegationType()) {
			case DELEGATE_FIRST:
				c = loadClassDelegateFirst(name);
				break;
			case DELEGATE_LATER:
				c = loadClassDelegateLater(name);
				break;
			}

			if (c != null) {
				return c;
			}

			c = loadedClasses.get(name);
			if (c != null) {
				return c;
			}

			// Find class file using this classloader and define in Metaspace

			if (sm != null) {
				sm.checkPackageDefinition(pkg);

				byte[] data = loadClassFromDisk(name);
				if (data != null) {
					c = defineClass(name, data, 0, data.length);
					return c;
				}
			}

			// Then, Check classloader of app dependencies

			for (int i = 0; i < appDependencies.length; i++) {
				try {
					c = AppProvisioner.get().getClassloader(appDependencies[i]).loadClass(name);
					break;
				} catch (ClassNotFoundException e1) {
				}
			}

			if (c != null) {
				loadedClasses.put(name, c);
				return c;
			}

			Exceptions.throwRuntime(new ClassNotFoundException(name));
			return null;
		}

	}

	@BlockerBlockerTodo("Add support for nested classes, as this may not work in that scenario")
	private byte[] loadClassFromDisk(String className) {

		try {

			// read class
			InputStream is = Files.newInputStream(path.resolve(className.replace(".", "/") + ".class"));
			ByteArrayOutputStream byteSt = new ByteArrayOutputStream();
			// write into byte
			int len = 0;
			while ((len = is.read()) != -1) {
				byteSt.write(len);
			}
			// convert into byte array
			return byteSt.toByteArray();

		} catch (IOException e) {

			if (!(e instanceof FileNotFoundException)) {
				Exceptions.throwRuntime(e);
			}

			return null;
		}
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
	}

}
