package com.re.paas.internal.classes;

import java.io.Serializable;

@FunctionalInterface
public interface BooleanSupplier extends java.util.function.BooleanSupplier, Serializable {
	boolean getAsBoolean();
}
