package com.re.paas.internal.runtime.spi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.re.paas.api.Platform;
import com.re.paas.api.fusion.BaseComponent;
import com.re.paas.api.runtime.RuntimeIdentity;
import com.re.paas.api.runtime.SystemClassLoader;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.infra.filesystem.FileSystemWrapper;

public class CustomClassLoader extends ClassLoader {

	// These are classes that we should always delegate to the jvm classloader
	private static List<String> extrinsicClasses = new ArrayList<>();

	// These are packages that should be treated specially. If enableForwarding is
	// true, loading of classes in these packages will delegated back to the system
	// classloader
	private static List<String> metaPackages = new ArrayList<>();

	private boolean enableForwarding = true;

	private static ClassLoader acl;

	private URI path;

	private final Boolean pool;

	private final Map<String, Class<?>> _classes = new HashMap<>();
	private final Map<String, byte[]> _classBytes = new HashMap<>();

	private BiConsumer<Class<?>, byte[]> listener;
	
	private final String name;
	
	CustomClassLoader(Boolean pool, String name) {
		super(CustomClassLoader.acl);

		this.pool = pool;
		this.name = name;

		try {

			this.path = CustomClassLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI();

		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	CustomClassLoader(Boolean pool) {
		this(pool, null);
	}

	static void setJvmAppClassLoader(ClassLoader cl) {
		if (acl == null) {
			acl = cl;
		}
	}

	static ClassLoader getJvmAppClassLoader() {
		return acl;
	}

	CustomClassLoader disableForwarding() {
		this.enableForwarding = false;
		return this;
	}

	@Override
	public String getName() {
		return this.name != null ? this.name : super.getName();
	}

	CustomClassLoader listener(BiConsumer<Class<?>, byte[]> listener) {
		this.listener = listener;
		return this;
	}

	@Override
	public final Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

		if (extrinsicClasses.contains(name)) {
			return getParent().loadClass(name);
		}

		if (this.enableForwarding) {
			for (String pkg : metaPackages) {
				if (name.startsWith(pkg + ".")) {
					return ClassLoader.getSystemClassLoader().loadClass(name);
				}
			}
		}

		if (this.pool) {
			Class<?> clazz = _classes.get(name);
			if (clazz != null) {
				return clazz;
			}
		}

		synchronized (getClassLoadingLock(name)) {

			// First, check if the class has already been loaded
			Class<?> c = findLoadedClass(name);
			if (c == null) {

				// The attempt to find it in classpath
				c = findClass(name);

				if (c == null && this.getParent() != null) {
					// If still not found, then invoke the parent's loadClass(..) in order
					// to find the class.
					c = this.getParent().loadClass(name);
				}
			}
			return c;
		}
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {

		// System.out.println("finding " + name);

		byte[] data = loadClassFromDisk(name);
		if (data == null) {
			return null;
		}

		// System.out.println("defining " + name);
		CustomClassLoader cl = this.pool ? new CustomClassLoader(false) : this;
		Class<?> clazz = cl.defineNewClass(name, data, 0, data.length, null);

		if (pool) {
			this.ingest(clazz, data);
		}

		if (this.listener != null) {
			this.listener.accept(clazz, data);
		}

		return clazz;
	}

	@Override
	public URL getResource(String name) {

		if (!RuntimeIdentity.getInstance().isTrusted(1)) {
			throw new SecurityException("Unable to get resource: " + name);
		}

		return this.getParent().getResource(name);
	}

	/**
	 * This ingests the classes loaded by the specified classloader into this pool
	 * 
	 * @param cl
	 */
	void ingest(String name, byte[] bytecode, ProtectionDomain pd) {
		CustomClassLoader cl = new CustomClassLoader(false);
		Class<?> clazz = cl.defineNewClass(name, bytecode, 0, bytecode.length, pd);
		this.ingest(clazz, bytecode);
	}

	void ingest(Class<?> clazz, byte[] bytecode) {
		// System.out.println("Ingesting " + c.getName() + " into classloader pool");
		this._classes.put(ClassUtils.getName(clazz), clazz);
		this._classBytes.put(ClassUtils.getName(clazz), bytecode);
	}

	void prune() {
		_classes.clear();
		_classBytes.clear();
	}

	Class<?> defineNewClass(String name, byte[] b) {
		return this.defineClass(name, b, 0, b.length, null);
	}

	Class<?> defineNewClass(String name, byte[] b, int off, int len, ProtectionDomain pd) {
		return this.defineClass(name, b, off, len, pd);
	}

	/**
	 * This returns all classes contained in this pool. It is important to note all
	 * classes may not contain the same classloader due to bytecode instrumentation
	 * 
	 * @return
	 */
	List<Class<?>> getClasses() {
		return new ArrayList<>(_classes.values());
	}

	Map<String, Class<?>> getClassesMap() {
		return _classes;
	}

	Class<?> getClass(String name) {
		return _classes.get(name);
	}

	byte[] getClassBytes(String name) {
		return _classBytes.get(name);
	}

	File getClassFile(String className) {
		return new File(this.path.resolve(className.replace(".", File.separator) + ".class"));
	}

	CustomClassLoader setPath(URI path) {
		this.path = path;
		return this;
	}

	private byte[] loadClassFromDisk(String className) {

		try {

			InputStream in = new FileInputStream(getClassFile(className));
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			int len = 0;
			while ((len = in.read()) != -1) {
				out.write(len);
			}
			in.close();
			return out.toByteArray();

		} catch (IOException e) {
			return null;
		}
	}

	static {

		extrinsicClasses.add(ClassUtils.getName(Application.class));
		extrinsicClasses.add(ClassUtils.getName(CustomClassLoader.class));
		extrinsicClasses.add(ClassUtils.getName(SystemClassLoader.class));
		extrinsicClasses.add(ClassUtils.getName(SystemClassLoaderImpl.class));
		extrinsicClasses.add(ClassUtils.getName(FileSystemWrapper.class));
		extrinsicClasses.add(ClassUtils.getName(BaseComponent.class));

		metaPackages.add(Platform.COMPONENT_BASE_PKG);
	}
}
