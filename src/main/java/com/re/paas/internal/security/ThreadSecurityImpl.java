package com.re.paas.internal.security;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.threadsecurity.ThreadSecurity;

public class ThreadSecurityImpl extends ThreadSecurity {

	private static Thread mainThread;
	private static volatile List<Long> trustedThreads = new ArrayList<Long>();

	private static ThreadLocal<Boolean> isTrusted = ThreadLocal.withInitial(null);

	public ThreadSecurity setMainThread(Thread t) {
		if (mainThread == null) {
			mainThread = t;
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

	public Thread newTrustedThread(Runnable r) {
		assert isTrusted();
		return newThread(r, true, null);
	}
	
	public Thread newCommonThread(Runnable r) {
		assert !isTrusted();
		return newThread(r, false, null);
	}
	
	public Thread newCommonThread(Runnable r, AppClassLoader cl) {
		assert isTrusted();
		return newThread(r, false, cl);
	}

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
			if(trust) {
				trustedThreads.add(t.getId());
			} else {
				t.setContextClassLoader(cl);
			}
		}
		
		return t;
	}

}
