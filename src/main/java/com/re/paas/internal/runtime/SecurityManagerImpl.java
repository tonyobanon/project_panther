package com.re.paas.internal.runtime;

import java.security.Permission;

public class SecurityManagerImpl extends SecurityManager {

	/**
	 * 
	 * This field prevents a recursive call to the checkPermission(...) method
	 * <br><br>
	 * <b>ImplNotes:</b><br>
	 *  There is every possibility that a StackOverflowError or
	 * ClassCircularityError is thrown when this method is called. The actual error
	 * thrown depends on the classloader that loads this class. If it's the Jvm's
	 * AppClassLoder, it throws a StackOverflowError, else it throws a
	 * ClassCircularityError (re-thrown, because a StackOverflowError happened
	 * internally), hence when within the context of checkPermission(...), we want
	 * to bypass all subsequent calls to checkPermission(...)
	 */
	private static ThreadLocal<Boolean> activeContext = ThreadLocal.withInitial(() -> false);

	@Override
	public void checkPermission(Permission perm) {
		
		if (activeContext.get()) {
			return;
		}

		activeContext.set(true);
		try {
			Boolean allowed = Permissions.isAllowed(perm);

			if (!allowed) {
				throw new SecurityException("Permission was refused: " + perm.toString());
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		activeContext.set(false);
	}
}
