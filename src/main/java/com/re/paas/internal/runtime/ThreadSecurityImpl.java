package com.re.paas.internal.runtime;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.runtime.Invokable;
import com.re.paas.api.runtime.ThreadSecurity;

public class ThreadSecurityImpl extends ThreadSecurity {

	private ClassLoader mainClassloader;

	public ThreadSecurityImpl() {
		this(ThreadSecurityImpl.class.getClassLoader());
	}

	public ThreadSecurityImpl(ClassLoader cl) {
		mainClassloader = cl;
	}

	public Boolean isTrusted() {
		return getClass().getClassLoader().equals(mainClassloader);
	}

	@BlockerTodo("Create mechanism to monitor the number of spawned threads per application..")
	@BlockerTodo("Enforce a timeout policy, so that calls to i.call() don't take forever..")
	
	public <R> Thread newThread(Invokable<R> i) {
		Permissions.bypass.set(true);
		Thread t = new Thread(() -> {
			Permissions.init();
			i.call();
		});
		Permissions.bypass.set(false);
		return t;
	}

}
