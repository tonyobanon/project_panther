package com.re.paas.internal.classes;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.spi.BaseSPILocator.ShuffleStrategy;
import com.re.paas.api.runtime.spi.ClassIdentityType;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.runtime.spi.Classpaths;

public class ClasspathScanner<T> {

	private static final Logger LOG = Logger.get(ClasspathScanner.class);

	private Map<ShuffleStrategy, Function<ArrayList<Class<? extends T>>, ArrayList<Class<? extends T>>>> shuffleStrategies = new HashMap<>();

	private final ClassLoader cl;

	private final String fileExtension;
	private final Iterable<String> nameSuffixes;
	private final ClassIdentityType classIdentityType;
	private final Class<T> classType;
	private ShuffleStrategy shuffleStrategy = ShuffleStrategy.HIGHER_DEPTH;

	private int maxCount = -1;

	private boolean loadAbstractClasses = false;
	private boolean accessAllConstructors = false;

	/**
	 * This constructor should be used for XML and JSON artifacts
	 */
	public ClasspathScanner(Iterable<String> nameSuffixes, String fileExtension, Class<T> type) {

		this.nameSuffixes = nameSuffixes;
		this.fileExtension = fileExtension;

		this.classIdentityType = null;
		
		this.cl = type.getClassLoader();
		this.classType = type;
	}

	public ClasspathScanner(Class<T> type, ClassLoader cl) {
		this(Lists.newArrayList(""), type, ClassIdentityType.ASSIGNABLE_FROM, cl);
	}

	public ClasspathScanner(String nameSuffix, Class<T> type) {
		this(nameSuffix, type, ClassIdentityType.ASSIGNABLE_FROM);
	}

	public ClasspathScanner(String nameSuffix, Class<T> type, ClassIdentityType identityType) {
		this(Lists.newArrayList(nameSuffix), type, identityType, ClassLoader.getSystemClassLoader());
	}

	/**
	 * This constructor should be used for classes
	 */
	public ClasspathScanner(Iterable<String> nameSuffixes, Class<T> type, ClassIdentityType identityType, ClassLoader cl) {

		this.nameSuffixes = nameSuffixes;
		this.fileExtension = "class";

		this.classIdentityType = identityType;
		
		this.cl = cl;
		this.classType = type;

		addShuffleStategies();
	}

	/**
	 * This constructor should be used for classes
	 */
	public ClasspathScanner(Class<T> type, ClassIdentityType identityType) {
		// Todo: null should be an actual iterable of ""?
		this((Iterable<String>) null, type, identityType, ClassLoader.getSystemClassLoader());
	}
	
	public ClasspathScanner(Class<T> type, ClassIdentityType identityType, ClassLoader cl) {
		// Todo: null should be an actual iterable of ""?
		this((Iterable<String>) null, type, identityType, cl);
	}

	public List<Class<? extends T>> scanClasses() {

		if (classIdentityType == null || classType == null) {
			return new ArrayList<>();
		}
		
		
		final ArrayList<Class<? extends T>> classes = new ArrayList<Class<? extends T>>();

		// Scan classpath

		try {
		
			ClassLoader cl = this.cl;
			Path basePath = Classpaths.get(cl);

			Files.walkFileTree(basePath, new SimpleFileVisitor<Path>() {
				@SuppressWarnings("unchecked")
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

					String fullyQualifiedName = basePath.relativize(file).toString();

					boolean hasNameSuffix = false;

					for (String nameSuffix : nameSuffixes) {
						if (fullyQualifiedName.endsWith(nameSuffix + "." + fileExtension)) {
							hasNameSuffix = true;
						}
					}

					if (!hasNameSuffix) {
						return FileVisitResult.CONTINUE;
					}

					String className = fullyQualifiedName.replaceAll(Pattern.quote(File.separator), ".")
							.replaceAll(".class\\z", "");

					try {

						Class<? extends T> clazz = null;

						try {
							clazz = (Class<? extends T>) cl.loadClass(className);
						} catch (Error e) {
							
							// Possibly some compilation problem, skip
							// System.out.println("skipping " + className);
							
							return FileVisitResult.CONTINUE;
						}

						if (classType.equals(clazz)) {
							LOG.trace("Skipping base type " + ClassUtils.asString(clazz));
							return FileVisitResult.CONTINUE;
						}

						if (!loadAbstractClasses) {
							if (Modifier.isAbstract(clazz.getModifiers())
									|| Modifier.isInterface(clazz.getModifiers())) {
								LOG.trace("Skipping abstract type " + ClassUtils.asString(clazz));
								return FileVisitResult.CONTINUE;
							}
						}

						if (!loadAbstractClasses) {

							boolean hasNoArgConstructor = false;

							for (Constructor<?> constr : accessAllConstructors ? clazz.getDeclaredConstructors()
									: clazz.getConstructors()) {
								if (constr.getParameterCount() == 0 && Modifier.isPublic(constr.getModifiers())) {
									hasNoArgConstructor = true;
									break;
								}
							}

							if (!hasNoArgConstructor) {
								// LOG.warn("Class: " + ClassUtils.toString(clazz) + " has no no-arg constructor
								// and will be skipped ..");
								return FileVisitResult.CONTINUE;
							}
						}
						

						boolean b = false;

						switch (classIdentityType) {

						case ASSIGNABLE_FROM:
							b = classType.isAssignableFrom(clazz) && !ClassUtils.equals(classType, clazz);
							break;

						case DIRECT_SUPER_CLASS:
							b = ClassUtils.isDirectChild(classType, clazz);
							break;

						}

						if (b) {
							classes.add((Class<? extends T>) clazz);
						}

					} catch (Exception e) {
						// Logger.get().error(e.getMessage());
					}

					if (maxCount != -1 && maxCount >= classes.size()) {
						return FileVisitResult.TERMINATE;
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path file, IOException e) throws IOException {
					if (e == null) {
						return FileVisitResult.CONTINUE;
					} else {
						// directory iteration failed
						throw e;
					}
				}
			});

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

		if (classIdentityType != ClassIdentityType.ASSIGNABLE_FROM) {
			return classes;
		}

		return shuffleStrategies.get(getShuffleStrategy()).apply(classes);
	}

	public int getMaxCount() {
		return maxCount;
	}

	public ClasspathScanner<T> setMaxCount(int maxCount) {
		this.maxCount = maxCount;
		return this;
	}

	public ClassLoader getClassLoader() {
		return cl;
	}

	public ShuffleStrategy getShuffleStrategy() {
		return shuffleStrategy;
	}

	public ClasspathScanner<T> setShuffleStrategy(ShuffleStrategy shuffleStrategy) {
		this.shuffleStrategy = shuffleStrategy;
		return this;
	}

	private void addShuffleStategies() {

		shuffleStrategies.put(ShuffleStrategy.HIGHER_DEPTH, (p) -> {
			return ClassUtils.reshuffleByDepth(classType, p, true);
		});

		shuffleStrategies.put(ShuffleStrategy.LOWER_DEPTH, (p) -> {
			return ClassUtils.reshuffleByDepth(classType, p, false);
		});
	}

	/**
	 * This indicates whether this classpath scanner can discover interfaces and
	 * abstract classes
	 * 
	 * @param loadAbstractClasses
	 * @return
	 */
	public ClasspathScanner<T> setLoadAbstractClasses(boolean loadAbstractClasses) {
		this.loadAbstractClasses = loadAbstractClasses;
		return this;
	}

	public ClasspathScanner<T> setAccessAllConstructors(boolean accessAllConstructors) {
		this.accessAllConstructors = accessAllConstructors;
		return this;
	}

}
