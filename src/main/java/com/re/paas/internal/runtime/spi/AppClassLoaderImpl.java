package com.re.paas.internal.runtime.spi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.google.common.collect.Maps;
import com.re.paas.api.annotations.develop.BlockerBlockerTodo;
import com.re.paas.api.annotations.develop.Prototype;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.runtime.ClassLoaderSecurity;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.classes.AppDirectory;

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

	public AppClassLoaderImpl(ClassLoader parent, String appId) {
		this(parent, appId, null);
	}

	public AppClassLoaderImpl(ClassLoader parent, String appId, String[] appDependencies) {
		super(parent);
		this.appId = appId;
		this.path = AppProvisionerImpl.getAppBasePath().resolve(appId).resolve("classes");
		this.appDependencies = appDependencies;

		// Load ThreadSecurity class into the classloader
		ClassLoaderSecurity.load(this);
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

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] data = loadClassFromDisk(name);
		if (data == null) {
			return null;
		}

		return this.defineClass(name, data, 0, data.length, null);
	}

	private Class<?> findClass0(String name) throws ClassNotFoundException {
		Class<?> c = null;

		// First, check if the class has already been loaded by the current classloader
		c = findLoadedClass(name);

		if (c != null) {
			return c;
		}

		// Find class
		c = findClass(name);

		return c;
	}

	private Class<?> loadClassDelegateFirst(String name) throws ClassNotFoundException {

		Class<?> c = null;

		// First, Delegate to parent classloader
		try {
			c = super.loadClass(name, false);
		} catch (ClassNotFoundException e) {
		}

		if (c != null) {
			return c;
		}

		return findClass0(name);
	}

	private Class<?> loadClassFindFirst(String name) throws ClassNotFoundException {

		Class<?> c = findClass0(name);

		if (c != null) {
			return c;
		}

		// Then, Delegate to parent classloader
		try {
			c = super.loadClass(name, false);
		} catch (ClassNotFoundException e) {
		}

		return c;
	}

	@Override
	public Class<?>[] load(Class<?>... classes) throws ClassNotFoundException {
		Class<?>[] r = new Class<?>[classes.length];
		for(int i = 0; i < classes.length; i++) {
			Class<?> c = this.loadClass(classes[i].getName());
			r[i] = c;
		}
		return r;
	}
	

	@Override
	public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

		String pkg = ClassUtils.getPackageName(name);
		SecurityManager sm = System.getSecurityManager();

		if (sm != null) {
			// Here, we are using the className instead of pkg, because we also want to
			// check for classes as well
			sm.checkPackageAccess(name);
		}

		synchronized (getClassLoadingLock(name)) {

			Class<?> c = null;
			DelegationType delegateType = getDelegationType(name);

			switch (delegateType) {
			case DELEGATE_FIRST:
				c = loadClassDelegateFirst(name);
				break;
			case FIND_FIRST:
				c = loadClassFindFirst(name);
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

		Path path = AppDirectory.getBasePath().resolve(className.replace(".", "/") + ".class");

		if (!Files.exists(path)) {

			path = this.path.resolve(className.replace(".", "/") + ".class");

			if (!Files.exists(path)) {
				return null;
			}
		}

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
