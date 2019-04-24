package com.re.paas.api.runtime;

import com.re.paas.api.annotations.ApplicationInstrinsic;

@FunctionalInterface
@ApplicationInstrinsic
public interface Invokable<T> {
	T call();
}
