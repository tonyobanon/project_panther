package com.re.paas.internal;

import com.re.paas.api.annotations.ProtectionContext;

public class X {

	@ProtectionContext
	public void doStuff(String name) {
		System.out.println("hello " + name);
	}
	
}
