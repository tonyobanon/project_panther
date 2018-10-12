package com.re.paas.api.threadsecurity;

import com.re.paas.api.app_provisioning.AppClassLoader;

public abstract class ThreadSecurity {
	
	private static ThreadSecurity instance;
	
	public static ThreadSecurity get() {
		return instance;
	}
	
	public static void setInstance(ThreadSecurity instance) {
		if(ThreadSecurity.instance == null) {
			ThreadSecurity.instance = instance;
		}
	}
	
	public static String getAppId() {
		
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		String appId = null;
		
		if(cl instanceof AppClassLoader) {
			((AppClassLoader) cl).getAppId();
		}
		
		return appId;
	}
	
	public static boolean hasTrust() {
		return get().isTrusted();
	}
	
	public abstract ThreadSecurity setMainThread(Thread t);

	public abstract Boolean isTrusted();

	/**
	 * This requests that the current thread should run in a privileged context.
	 */
	public abstract ThreadSecurity trust();

	public abstract Thread newTrustedThread(Runnable r);
	
	public abstract Thread newCommonThread(Runnable r);
	
	/**
	 * This is typically used when a trusted thread wants to create a common thread
	 * @param r
	 * @param cl
	 * @return
	 */
	public abstract Thread newCommonThread(Runnable r, AppClassLoader cl);
	
}