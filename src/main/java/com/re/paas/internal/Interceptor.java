package com.re.paas.internal;

import java.lang.StackWalker.StackFrame;
import java.lang.reflect.Executable;
import java.util.Arrays;

import com.re.paas.api.annotations.ProtectionContext;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.internal.jvmtools.annotations.AnnotationUtil;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

public class Interceptor {

	@RuntimeType
	@BlockerTodo("Implement")
	@BlockerTodo("Moving forward, use singleton to store customValidator instances")
	public static void intercept(@Origin Executable method, @AllArguments Object[] args) {

		StackWalker sw = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
		ObjectWrapper<StackFrame> frame = new ObjectWrapper<>();

		sw.walk(stackFrames -> {
			frame.set(stackFrames.limit(3).skip(2).findFirst().get());
			return null;
		});

		ProtectionContext ctx = (ProtectionContext) AnnotationUtil.getAnnotation(method, ProtectionContext.class);

		System.out.println("intercepted, args=" + Arrays.toString(args) + ", caller-class=" + frame.get().getDeclaringClass().getName()
				+ ", caller-method=" + frame.get().getMethodName() + ", target-class=" + method.getDeclaringClass()
				+ ", target-method=" + method.getName() + ", annotation=" + ctx);
	}
}
