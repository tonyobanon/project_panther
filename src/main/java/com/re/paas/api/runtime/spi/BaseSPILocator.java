package com.re.paas.api.runtime.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Pattern;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.runtime.MethodMeta;
import com.re.paas.api.runtime.MethodMeta.Factor;
import com.re.paas.api.runtime.MethodMeta.IdentityStrategy;
import com.re.paas.internal.runtime.spi.SPILocatorHandlerImpl;

public abstract class BaseSPILocator {

	private static final Map<SpiType, List<String>> typeSuffixes = Collections.synchronizedMap(new HashMap<>());

	public abstract SpiType spiType();

	public final Iterable<String> classSuffix() {
		List<String> suffixes = typeSuffixes.get(spiType());

		if (suffixes == null) {
			return Collections.emptyList();
		}

		List<String> result = new ArrayList<>(suffixes.size());

		suffixes.forEach(e -> {
			String suffix = e.split("#")[1];
			if (!result.contains(suffix)) {
				result.add(suffix);
			}
		});
		return result;
	}

	public ClassIdentityType classIdentity() {
		return ClassIdentityType.ASSIGNABLE_FROM;
	}

	@MethodMeta(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SAME, allowed = SPILocatorHandlerImpl.class)
	public static final void removeTypeSuffixes(SpiType[] types, String appId) {
		for (SpiType type : types) {
			ListIterator<String> suffixIterator = getTypeSuffixes(type).listIterator();

			while (suffixIterator.hasNext()) {
				String e = suffixIterator.next();
				if (e.startsWith(appId + "#")) {
					suffixIterator.remove();
				}
			}
		}
	}

	@MethodMeta(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SAME, allowed = SPILocatorHandlerImpl.class)
	public static final void addTypeSuffix(SpiType type, String appId, String[] suffixes) {
		List<String> suffixList = getTypeSuffixes(type);
		for (String suffix : suffixes) {
			if (!Pattern.matches("[a-zA-Z]+", suffix)) {
				Exceptions.throwRuntime(new IllegalArgumentException("Incorrect suffix format"));
			}

			if (!suffixList.contains(suffix)) {
				suffixList.add(appId + "#" + suffix);
			}
		}
	}

	private static final List<String> getTypeSuffixes(SpiType type) {
		List<String> suffixes = typeSuffixes.get(type);
		if (suffixes == null) {
			suffixes = new ArrayList<>();
			typeSuffixes.put(type, suffixes);
		}
		return suffixes;
	}

	public abstract Class<? extends Resource> classType();

	public abstract Class<? extends SpiDelegate<?>> delegateType();

	public ShuffleStrategy shuffleStrategy() {
		return ShuffleStrategy.HIGHER_DEPTH;
	}

	public enum ShuffleStrategy {
		HIGHER_DEPTH, LOWER_DEPTH
	}

	static {
		for (SpiType type : SpiType.values()) {
			typeSuffixes.put(type, new ArrayList<>());
		}
	}
}
