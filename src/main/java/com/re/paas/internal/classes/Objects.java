package com.re.paas.internal.classes;

import java.util.function.Supplier;

public class Objects {

	public static <T> T anyOf(Supplier<T> first, Supplier<T> second) {
		T a = first.get();
		return a != null ? a : second.get();
	}

}
