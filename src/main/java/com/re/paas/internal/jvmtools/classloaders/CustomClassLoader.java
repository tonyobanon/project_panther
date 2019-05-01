package com.re.paas.internal.jvmtools.classloaders;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class CustomClassLoader extends ClassLoader {

	private static final ClassLoader scl = ClassLoader.getSystemClassLoader();

	private final Boolean pool;

	private final Map<String, Class<?>> _classes = new HashMap<>();
	private final Map<String, byte[]> _classBytes = new HashMap<>();

	private BiConsumer<Class<?>, byte[]> onFindListener;

	private final URI path;

	public CustomClassLoader() {
		this(false);
	}

	public CustomClassLoader onFind(BiConsumer<Class<?>, byte[]> onFindListener) {
		this.onFindListener = onFindListener;
		return this;
	}

	public CustomClassLoader(Boolean pool) {

		super(CustomClassLoader.scl);

		this.pool = pool;
		try {
			this.path = getResource(".").toURI();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public final Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

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

		if (this.onFindListener != null) {
			this.onFindListener.accept(clazz, data);
		}

		return clazz;
	}

	/**
	 * This ingests the classes loaded by the specified classloader into this pool
	 * 
	 * @param cl
	 */
	public void ingest(String name, byte[] bytecode, ProtectionDomain pd) {
		CustomClassLoader cl = new CustomClassLoader(false);
		Class<?> clazz = cl.defineNewClass(name, bytecode, 0, bytecode.length, pd);
		this.ingest(clazz, bytecode);
	}

	public void ingest(Class<?> clazz, byte[] bytecode) {
		// System.out.println("Ingesting " + c.getName() + " into classloader pool");
		this._classes.put(clazz.getName(), clazz);
		this._classBytes.put(clazz.getName(), bytecode);
	}

	public void prune() {
		_classes.clear();
		_classBytes.clear();
	}

	public Class<?> defineNewClass(String name, byte[] b, int off, int len, ProtectionDomain pd) {
		return this.defineClass(name, b, off, len, pd);
	}

	/**
	 * This returns all classes contained in this pool. It is important to note all
	 * classes may not contain the same classloader due to bytecode instrumentation
	 * 
	 * @return
	 */
	public List<Class<?>> getClasses() {
		return new ArrayList<>(_classes.values());
	}

	public Map<String, Class<?>> getClassesMap() {
		return _classes;
	}

	public Class<?> getClass(String name) {
		return _classes.get(name);
	}

	public byte[] getClassBytes(String name) {
		return _classBytes.get(name);
	}

	private File getClassFile(String className) {
		return new File(path.resolve(className.replace(".", "/") + ".class"));
	}

	public URI getPath() {
		return path;
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
}
