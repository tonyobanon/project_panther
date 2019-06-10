package com.re.paas.api.apps;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.designpatterns.Factory;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.runtime.Invokable;
import com.re.paas.api.runtime.MethodMeta;
import com.re.paas.api.runtime.ClassLoaderSecurity;
import com.re.paas.internal.fusion.services.impl.RoutingContextHandler;
import com.re.paas.internal.runtime.security.ClassLoaderSecurityImpl;

@BlockerTodo("In static block, do a real scan for @ApplicationInstrinsic types")
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

	@MethodMeta	
	public static void addDelegationType(String classname, DelegationType delegateType) {
		delegateTypes.put(classname, delegateType);
	}

	public static AppClassLoader get(String appId, String[] dependencies) {
		return Factory.get(AppClassLoader.class, new Object[] { appId, dependencies });
	}
	
	public abstract Class<?>[] load(Class<?>...classes) throws ClassNotFoundException;

	@MethodMeta
	public abstract Path getPath();

	public abstract String getAppId();

	public abstract boolean isStopping();

	@MethodMeta
	public abstract void setStopping(boolean isStopping);

	public static enum DelegationType {
		DELEGATE_FIRST, FIND_FIRST
	}

	static {

		LOG.debug("Scanning for @ApplicationInstrinsic types");
		
		addDelegationType(ClassLoaderSecurity.class.getName(), DelegationType.FIND_FIRST);
		addDelegationType(ClassLoaderSecurityImpl.class.getName(), DelegationType.FIND_FIRST);

		addDelegationType(Invokable.class.getName(), DelegationType.FIND_FIRST);
		
		addDelegationType(RoutingContextHandler.class.getName(), DelegationType.FIND_FIRST);
	}
}
