package com.re.paas.internal.runtime.spi;

import java.net.URL;

import com.re.paas.api.Platform;
import com.re.paas.api.runtime.SystemClassLoader;

public class SystemClassLoaderImpl extends ClassLoader implements SystemClassLoader {

	/* Default Classloader */
	private CustomClassLoader cl;

	private boolean sealed = false;

	public SystemClassLoaderImpl(ClassLoader cl) {
		super(null);
		CustomClassLoader.setJvmAppClassLoader(cl);
	}

	private void createClassLoader() {
		this.cl = new CustomClassLoader(false);

		FusionClassloaders.init();
	}

	void setClassLoader(CustomClassLoader cl) {

		if (this.sealed) {
			throw new RuntimeException(this + " is sealed");
		}

		this.cl = cl;
	}
	
	void appendToClassPathForInstrumentation(String p) {
		cl.appendToClassPathForInstrumentation(p);
	}

	@Override
	protected final Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

		if (this.cl == null) {
			createClassLoader();
		}
		
		if (name.startsWith(Platform.getComponentBasePkg() + ".")) {
			// If this is a component class, load it specially
			return FusionClassloaders.loadComponentClass(name);
		}

		return cl.loadClass(name, resolve);
	}

	@Override
	protected final Class<?> findClass(String name) throws ClassNotFoundException {

		if (this.cl == null) {
			createClassLoader();
		}

		return cl.findClass(name);
	}

	ClassLoader getClassLoader() {
		return this.cl;
	}

	void seal() {
		sealed = true;
	}

	@Override
	public URL getResource(String name) {
		return CustomClassLoader.getJvmAppClassLoader().getResource(name);
	}

	public Boolean isPlatformClass(Class<?> clazz) {
		return 
				// Pre transformation
				clazz.getClassLoader() == CustomClassLoader.getJvmAppClassLoader() ||
				// Post transformation
				clazz.getClassLoader().getClass() == CustomClassLoader.class;
	}
	
}
