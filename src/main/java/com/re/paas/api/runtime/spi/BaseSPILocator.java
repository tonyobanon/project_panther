package com.re.paas.api.runtime.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseSPILocator {

	private static Map<SpiType, List<String>> typeSuffixes = Collections.synchronizedMap(new HashMap<>());

	public abstract SpiType spiType();

	public final Iterable<String> classSuffix() {
		return typeSuffixes.get(spiType());
	}

	public ClassIdentityType classIdentity() {
		return ClassIdentityType.ASSIGNABLE_FROM;
	}

	protected void addTypeSuffix(String suffix) {
		addTypeSuffix(spiType(), suffix);
	}

	public static void addTypeSuffix(SpiType type, String suffix) {
		List<String> suffixes = typeSuffixes.get(type);
		if (!suffixes.contains(suffix)) {
			suffixes.add(suffix);
		}
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
