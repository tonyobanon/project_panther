package com.re.paas.internal.runtime;

import java.lang.StackWalker.StackFrame;
import java.util.Iterator;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.IntegerWrapper;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.internal.classes.ClassUtil;

public class StackFrameUtilImpl {

	// private static final Logger LOG = LoggerFactory.get().getLog(StackFrameUtilImpl.class);
	private static final Integer MAX_SYNTHETIC_SKIPS = 25;
	

	public static StackFrame getCaller(Integer skipsOffset, Boolean jvmInstrinsic, Boolean retainReference) {
		
		Permissions.bypass.set(true);
		StackWalker sw = retainReference ? StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
				: StackWalker.getInstance();
		Permissions.bypass.set(false);

		ObjectWrapper<StackFrame> frame = new ObjectWrapper<>();

		IntegerWrapper syntheticSkips = new IntegerWrapper();

		sw.walk(stackFrames -> {

			Iterator<StackFrame> it = stackFrames.skip(skipsOffset).iterator();

			if (jvmInstrinsic) {

				// Here, the Jvm can be the real culprit of a call, so we want to
				// get the caller (irrespective). This is common in the case of
				// Java service providers

				frame.set(it.next());

			} else {

				// Here, only non-Jvm classes can be the real culprit of a call

				// 1. Normal "privileged" invocations within untrusted third-party POJO
				// resources

				// 2. Trying to use the jvm to access some protected system resources that the
				// security manager would otherwise not allow. Here, we want to know the non-jvm
				// caller knowing fully well that the jvm cannot be the real culprit, even it
				// seems like it is

				while (it.hasNext()) {

					StackFrame f = it.next();

					if (!ClassUtil.isJvmFrame(f)) {
						frame.set(f);
						break;
					}
				}
			}

			// Now that we have computed the real caller frame, we need to make sure that
			// it's non-synthetic. If it's synthetic, we bubble-up until we find it's
			// non-synthetic ancestor

			if (ClassUtil.isFrameSynthetic(frame.get())) {

				StackFrame f = it.next();

				while (ClassUtil.isFrameSynthetic(f) || ClassUtil.isJvmFrame(f)) {

					if (MAX_SYNTHETIC_SKIPS > 0 && syntheticSkips.get().intValue() >= MAX_SYNTHETIC_SKIPS) {
						Exceptions.throwRuntime("Maxiumum synthetic skips reached.. ");
					}

					f = it.next();
					syntheticSkips.add();
				}

				// LOG.debug("Returning non-synthetic frame: " + f + " instead of " + frame.get());

				// Finally re-assign
				frame.set(f);
			}
			
			
			String[] isSyntheticFrame = frame.get().getMethodName().split("\\Q$original$\\E");
			
			if (isSyntheticFrame.length == 2) {
				
				StackFrame next = it.next();
				
				assert next.getMethodName().equals(isSyntheticFrame[0]);
				
				frame.set(next);
			}
			
			return null;
		});

		return frame.get();
	}

}
