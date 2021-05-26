package com.re.paas.internal.runtime.spi;

import java.util.List;
import java.util.Map;

public abstract class ClassTransformer {

	public abstract void apply(CustomClassLoader cl, Map<String, List<String>> classes);

	public boolean applies() {
		return true;
	}

}
