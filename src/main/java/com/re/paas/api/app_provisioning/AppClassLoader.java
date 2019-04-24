package com.re.paas.api.app_provisioning;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.re.paas.api.annotations.ProtectionContext;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.designpatterns.Factory;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.runtime.Consumer;
import com.re.paas.api.runtime.Invokable;
import com.re.paas.api.runtime.ThreadSecurity;
import com.re.paas.internal.runtime.ThreadSecurityImpl;

@BlockerTodo("In static block, scan for @ApplicationInstrinsic type, inorder to set a delegation model of DelegationType.FIND_FIRST")
public abstract class AppClassLoader extends ClassLoader {

	private static Logger LOG = LoggerFactory.get().getLog(AppClassLoader.class);

	private static final DelegationType delegateType = DelegationType.DELEGATE_FIRST;

	protected static Map<String, DelegationType> delegateTypes = new HashMap<>();

	public AppClassLoader(ClassLoader parent) {
		super(parent);
	}

	public static DelegationType getDelegationType() {
		return delegateType;
	}

	public static DelegationType getDelegationType(String classname) {
		DelegationType delegateType = delegateTypes.get(classname);
		if (delegateType == null) {
			delegateType = getDelegationType();
		}
		return delegateType;
	}

	public static AppClassLoader get(Path path, String appId, String[] dependencies) {
		return Factory.get(AppClassLoader.class, new Object[] { path, appId, dependencies });
	}

	@ProtectionContext
	public abstract Path getPath();

	public abstract String getAppId();

	public abstract boolean isStopping();

	@ProtectionContext
	public abstract void setStopping(boolean isStopping);

	public static enum DelegationType {
		DELEGATE_FIRST, FIND_FIRST
	}

	static {

		LOG.debug("Scanning for @ApplicationInstrinsic types");

		delegateTypes.put(ThreadSecurity.class.getName(), DelegationType.FIND_FIRST);
		delegateTypes.put(ThreadSecurityImpl.class.getName(), DelegationType.FIND_FIRST);

		delegateTypes.put(Invokable.class.getName(), DelegationType.FIND_FIRST);
		delegateTypes.put(Consumer.class.getName(), DelegationType.FIND_FIRST);
		delegateTypes.put(BiConsumer.class.getName(), DelegationType.FIND_FIRST);
	}
}
