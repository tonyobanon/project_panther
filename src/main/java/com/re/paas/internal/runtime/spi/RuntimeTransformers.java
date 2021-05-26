package com.re.paas.internal.runtime.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.re.paas.api.Singleton;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.api.runtime.spi.BaseSPILocator.ShuffleStrategy;
import com.re.paas.api.utils.JsonParser;
import com.re.paas.internal.classes.ClasspathScanner;
import com.re.paas.internal.logging.DefaultLoggerFactory;
import com.re.paas.internal.utils.JsonParserImpl;

import javassist.CannotCompileException;
import javassist.NotFoundException;

public class RuntimeTransformers {

	private static final List<ClassTransformer> classTransformers = new ArrayList<>();
	static Logger LOG;

	/**
	 * This discovers methods with a {@link SecureMethod}, and copies the annotation
	 * from the super class to the subclass
	 * 
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	public static void apply() {

		// We need to eagerly register the singletons needed, because it is too
		// early for AppDelegate to do so
		
		Singleton.register(LoggerFactory.class, new DefaultLoggerFactory());
		Singleton.register(JsonParser.class, new JsonParserImpl());
		
		LOG = Logger.get(RuntimeTransformers.class);

		CustomClassLoader cl = new CustomClassLoader(true);
		Map<String, List<String>> classes = getClasses(cl);

		for (ClassTransformer transformer : classTransformers) {
			
			LOG.debug("Invoking runtime transformer: " + transformer.getClass());
			transformer.apply(cl, classes);
		}
	}
	

	private static Map<String, List<String>> getClasses(CustomClassLoader classloader) {

		CustomClassLoader cl = classloader.getClassesMap().isEmpty()
				? new CustomClassLoader(false).listener((clazz, bytes) -> {
					classloader.ingest(clazz, bytes);
				})

				: classloader;

		List<Class<?>> abstractClasses = newClasspathScanner(cl).setAccessAllConstructors(true)
				.setLoadAbstractClasses(true).scanClasses();

		Map<String, List<String>> result = new HashMap<>(abstractClasses.size());

		for (Class<?> c : abstractClasses) {

			List<String> implClasses = new ArrayList<>();

			for (Class<?> impl : cl.getClasses()) {

				if (

				c.isAssignableFrom(impl) &&

				// Due to the way that class bytes are constantly re-written to the custom
				// classloader, we have to compare with class names rather than direct object
				// comparison

						!c.getName().equals(impl.getName())) {

					implClasses.add(impl.getName());
				}
			}

			if (!implClasses.isEmpty()) {
				result.put(c.getName(), implClasses);
			}
		}

		return result;
	}

	private static ClasspathScanner<Object> newClasspathScanner(ClassLoader cl) {
		return (ClasspathScanner<Object>) newClasspathScanner(cl, Object.class);
	}

	private static <T> ClasspathScanner<T> newClasspathScanner(ClassLoader cl, Class<T> type) {
		return new ClasspathScanner<>(type, cl).setLoadAbstractClasses(true)
				.setShuffleStrategy(ShuffleStrategy.LOWER_DEPTH);
	}

	static {

		// Add default class transformers

		classTransformers.add(new ConcreteIntrinsicTransformer());
		classTransformers.add(new SecureMethodAnnotationTransformer());
		
		// Note: this should be last because here we call ClassLoaderUtil.setSystemClassLoder(...);
		classTransformers.add(new SecureMethodTransformer());
	}

}
