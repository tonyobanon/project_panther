package com.re.paas.internal.security;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.threadsecurity.ThreadSecurity;
import com.re.paas.api.utils.ClassUtils;

public class ThreadSecurityImpl extends ThreadSecurity {

	private static Thread mainThread;
	private static volatile List<Long> trustedThreads = new ArrayList<Long>();

	private static ThreadLocal<Boolean> isTrusted = ThreadLocal.withInitial(null);

	public ThreadSecurity init(Thread main) {
		if (mainThread == null) {
			mainThread = main;
		}
		return this;
	}

	public Boolean isTrusted() {
		Boolean b = isTrusted.get();
		return b != null && b;
	}

	public ThreadSecurity trust() {

		if (isTrusted != null) {
			return this;
		}

		Thread t = Thread.currentThread();

		if (t.equals(mainThread) || trustedThreads.contains(t.getId())) {
			isTrusted.set(true);
		} else {
			throw new SecurityException("Unable to acquire platform trust");
		}
		return this;
	}

	public void runTrusted(Runnable r) {
		assert isTrusted();
		return newThread(r, true, null);
	}

	public void runCommon(Runnable r, AppClassLoader cl) {
		assert isTrusted();
		return newThread(r, false, cl);
		
		
	}

	//TODO Use ThreadGroups, see comments in AppProvisionerImpl
	private Thread newThread(Runnable r, boolean trust, AppClassLoader cl) {
		
		boolean isTrusted = isTrusted();

		Thread t = new Thread(() -> {

			Thread childThread = Thread.currentThread();

			if (isTrusted && trust) {
				trust();
				boolean hasId = trustedThreads.remove(childThread.getId());
				assert hasId == true;
			} else {
				Permissions.initPermissions();
			}

			r.run();
		});

		if (isTrusted) {
			if (trust) {
				trustedThreads.add(t.getId());
			} else {
				t.setContextClassLoader(cl);
			}
		}

		return t;
	}
	
	
	/**
	 * This function should be used by SpiTypes that have an active classification
	 * type, to execute code originating from their resource classes.
	 * 
	 * The caller should check if the returned runnable is a thread, if it is
	 * 
	 * @param origin
	 * @param r
	 */
	public Runnable doRunnable(Class<?> origin, Runnable r) {

		boolean isTrusted = ThreadSecurity.hasTrust();
		boolean isSecure = ClassUtils.isTrusted(origin);

		if (isTrusted && !isSecure) {
			return ThreadSecurity.get().newCommonThread(r, (AppClassLoader) origin.getClassLoader());
		}

		return () -> {
			r.run();
		};
	}

}
