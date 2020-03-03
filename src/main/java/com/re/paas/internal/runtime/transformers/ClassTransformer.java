package com.re.paas.internal.runtime.transformers;

import java.util.List;
import java.util.Map;

import com.re.paas.internal.jvmtools.classloaders.CustomClassLoader;

public abstract class ClassTransformer {

	public abstract void apply(CustomClassLoader cl, Map<String, List<String>> classes);

	public boolean applies() {
		return true;
	}

}
