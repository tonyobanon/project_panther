package com.re.paas.internal.classes;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.Lists;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.spi.ClassIdentityType;
import com.re.paas.api.spi.BaseSPILocator.ShuffleStrategy;
import com.re.paas.api.utils.Utils;

public class ClasspathScanner<T> {

	private static final Logger LOG = Logger.get(ClasspathScanner.class);

	private Map<ShuffleStrategy, Function<List<Class<? extends T>>, List<Class<? extends T>>>> shuffleStrategies = new HashMap<>();
	private static final boolean allowAbstractTypes = false;

	private AppClassLoader classLoader;

	private final String fileExtension;
	private final Iterable<String> nameSuffixes;
	private final ClassIdentityType classIdentityType;
	private final Class<T> classType;
	private ShuffleStrategy shuffleStrategy = ShuffleStrategy.HIGHER_DEPTH;

	private int maxCount = -1;

	/**
	 * This constructor should be used for XML and JSON artifacts
	 */
	public ClasspathScanner(Iterable<String> nameSuffixes, String fileExtension, Class<T> type) {

		this.nameSuffixes = nameSuffixes;
		this.fileExtension = fileExtension;

		this.classIdentityType = null;
		this.classType = type;
	}

	public ClasspathScanner(String nameSuffix, Class<T> type, ClassIdentityType identityType) {
		this(Lists.newArrayList(nameSuffix), type, identityType);
	}
	
	/**
	 * This constructor should be used for classes
	 */
	public ClasspathScanner(Iterable<String> nameSuffixes, Class<T> type, ClassIdentityType identityType) {

		this.nameSuffixes = nameSuffixes;
		this.fileExtension = "class";

		this.classIdentityType = identityType;
		this.classType = type;
		
		addShuffleStategies();
	}

	/**
	 * This constructor should be used for classes
	 */
	public ClasspathScanner(Class<T> type, ClassIdentityType identityType) {
		this((Iterable<String>)null, type, identityType);
	}

	private static boolean isExtensionSiupported(String ext) {
		return /* ext.equals("json") || */ext.equals("xml");
	}

	public List<T> scanArtifacts() {

		if (!isExtensionSiupported(fileExtension)) {
			Exceptions.throwRuntime(new RuntimeException("The specified file extension is not supported"));
		}

		if (classType == null) {
			return new ArrayList<>();
		}

		final List<T> classes = new ArrayList<T>();

		// Scan classpath

		try {

			Path basePath = classLoader != null ? classLoader.getPath() : AppDirectory.getBasePath();

			Files.walkFileTree(basePath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

					boolean hasNameSuffix = false;

					for (String nameSuffix : nameSuffixes) {
						if (file.getFileName().toString().endsWith(nameSuffix + "." + fileExtension)) {
							hasNameSuffix = true;
						}
					}

					if (!hasNameSuffix) {
						return FileVisitResult.CONTINUE;
					}

					try {

						ObjectMapper xmlMapper = new XmlMapper();
						T o = xmlMapper.readValue(Utils.getString(file), classType);
						classes.add(o);

					} finally {
						// Do nothing
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

		return classes;

	}

	public List<Class<? extends T>> scanClasses() {

		if (classIdentityType == null || classType == null) {
			return new ArrayList<>();
		}

		final List<Class<? extends T>> classes = new ArrayList<Class<? extends T>>();

		// Scan classpath

		try {

			ClassLoader cl = classLoader != null ? classLoader : AppDirectory.getBaseClassloader();
			Path basePath = classLoader != null ? classLoader.getPath() : AppDirectory.getBasePath();

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

						Class<?> clazz = Class.forName(className, true, cl);

						if (classType.equals(clazz)) {
							LOG.debug("Skipping base type " + clazz.getName());
							return FileVisitResult.CONTINUE;
						}

						if (!allowAbstractTypes) {
							if (Modifier.isAbstract(clazz.getModifiers())
									|| Modifier.isInterface(clazz.getModifiers())) {
								LOG.debug("Skipping abstract type " + clazz.getName());
								return FileVisitResult.CONTINUE;
							}
						}

						if (clazz.getSuperclass() != null && clazz.getSuperclass().equals(Enum.class)) {
							return FileVisitResult.CONTINUE;
						}

						boolean hasNoArgConstructor = false;

						for (Constructor<?> constr : clazz.getConstructors()) {
							if (constr.getParameterCount() == 0) {
								hasNoArgConstructor = true;
								break;
							}
						}

						if (!hasNoArgConstructor) {
							LOG.warn("Class: " + clazz.getName() + " has no no-arg constructor and will be skipped ..");
							return FileVisitResult.CONTINUE;
						}

						switch (classIdentityType) {
						case ANNOTATION:
							if (clazz.isAnnotationPresent((Class<? extends Annotation>) classType)) {
								classes.add((Class<? extends T>) clazz);
							}
							break;
						case ASSIGNABLE_FROM:
							if (classType.isAssignableFrom(clazz) && !clazz.getName().equals(classType.getName())) {
								classes.add((Class<? extends T>) clazz);
							}
							break;
						case DIRECT_SUPER_CLASS:
							if (isDirectChild(clazz)) {
								classes.add((Class<? extends T>) clazz);
							}
							break;
						}

					} catch (ClassNotFoundException e) {
						Logger.get().error(e.getMessage());
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

		return shuffleStrategies.get(getShuffleStrategy()).apply(classes);
	}

	public int getMaxCount() {
		return maxCount;
	}

	public ClasspathScanner<T> setMaxCount(int maxCount) {
		this.maxCount = maxCount;
		return this;
	}

	public AppClassLoader getClassLoader() {
		return classLoader;
	}

	public ClasspathScanner<T> setClassLoader(AppClassLoader classLoader) {
		this.classLoader = classLoader;
		return this;
	}

	public ShuffleStrategy getShuffleStrategy() {
		return shuffleStrategy;
	}

	public ClasspathScanner<T> setShuffleStrategy(ShuffleStrategy shuffleStrategy) {
		this.shuffleStrategy = shuffleStrategy;
		return this;
	}

	/**
	 * This classes attempts to reshuffle the given list, such that higher-depth
	 * subclasses appear top in the returned list
	 * 
	 * @param classes
	 * @return
	 */
	private List<Class<? extends T>> reshuffleByDepth(List<Class<? extends T>> classes, boolean highestFirst) {

		if (classIdentityType != ClassIdentityType.ASSIGNABLE_FROM) {
			return classes;
		}

		Map<Class<? extends T>, Integer> classDepths = new HashMap<>();

		classes.forEach(c -> {
			classDepths.put(c, getInheritanceDepth(0, c));
		});

		LinkedList<Class<? extends T>> sortedMap = new LinkedList<>();

		Comparator<? super Entry<Class<? extends T>, Integer>> comparator = highestFirst
				? Map.Entry.comparingByValue(Comparator.reverseOrder())
				: Map.Entry.comparingByValue();

		classDepths.entrySet().stream().sorted(comparator).forEachOrdered(x -> sortedMap.add(x.getKey()));

		return sortedMap;
	}

	@SuppressWarnings("unchecked")
	private int getInheritanceDepth(int currentDepth, Class<? extends T> clazz) {

		boolean isDirectChild = isDirectChild(clazz);

		if (isDirectChild) {
			return currentDepth;
		}

		return getInheritanceDepth(currentDepth++, (Class<? extends T>) clazz.getSuperclass());
	}

	private boolean isDirectChild(Class<?> clazz) {

		if (classType.isInterface()) {
			if (Arrays.asList(clazz.getInterfaces()).contains(classType)) {
				return true;
			}
		} else {

			if (clazz.getSuperclass() != null && clazz.getSuperclass().equals(classType)
					&& !clazz.getName().equals(classType.getName())) {
				return true;
			}
		}
		return false;
	}

	private void addShuffleStategies() {

		shuffleStrategies.put(ShuffleStrategy.HIGHER_DEPTH, (p) -> {
			return reshuffleByDepth(p, true);
		});

		shuffleStrategies.put(ShuffleStrategy.LOWER_DEPTH, (p) -> {
			return reshuffleByDepth(p, false);
		});
	}

}