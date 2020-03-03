package com.re.paas.internal.runtime;

import java.lang.StackWalker.StackFrame;
import java.security.Permission;

import com.re.paas.api.classes.JvmConstants;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;

public class SecurityManagerImpl extends SecurityManager {

	private final Logger LOG;

	public SecurityManagerImpl() {
		LOG = LoggerFactory.get().getLog(SecurityManagerImpl.class);
	}

	/**
	 * 
	 * This field prevents a recursive call to the checkPermission(...) method <br>
	 * <br>
	 * <b>ImplNotes:</b><br>
	 * There is every possibility that a StackOverflowError or ClassCircularityError
	 * is thrown when this method is called. The actual error thrown depends on the
	 * classloader that loads this class. If it's the Jvm's AppClassLoder, it throws
	 * a StackOverflowError, else it throws a ClassCircularityError (re-thrown,
	 * because a StackOverflowError happened internally), hence when within the
	 * context of checkPermission(...), we want to bypass all subsequent calls to
	 * checkPermission(...)
	 */
	static ThreadLocal<Boolean> activeContext = ThreadLocal.withInitial(() -> false);

	@Override
	public void checkPermission(Permission perm) {
		
		if (activeContext.get()) {
			return;
		}

		activeContext.set(true);

		// We need to skip if this call originated from
		// java.lang.invoke.InnerClassLambdaMetafactory
		// as a result of lambdas being compiled to synthetic methods
		// by the Jvm

		StackWalker sw = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
		ObjectWrapper<Boolean> isSyntheticCall = new ObjectWrapper<Boolean>(false);

		sw.walk(stackFrames -> {

			StackFrame frame = stackFrames.limit(4).skip(3).findFirst().get();

			if (frame.getDeclaringClass().getName().equals(JvmConstants.LAMBDA_META_FACTORY_CLASS)) {
				isSyntheticCall.set(true);
			}

			return null;
		});

		if (isSyntheticCall.get()) {
			
			LOG.trace("Skipping synthetic call..");			
			activeContext.set(false);
			return;
		}

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
