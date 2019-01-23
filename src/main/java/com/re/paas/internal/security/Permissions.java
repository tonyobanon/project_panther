package com.re.paas.internal.security;

import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.annotations.Todo;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.classes.ClasspathScanner;

@Todo("Add permission sets for FilePermission, SocketPermission, as well as other perission types")
public class Permissions {

	private static final int PERMISSIONS_ARRAY_SIZE = 100;
	public static final Pattern DOT_PATTERN = Pattern.compile(Pattern.quote("."));

	private static Logger LOG = LoggerFactory.get().getLog(Permissions.class);

	public static final short ALLOW = -1;
	public static final short DENY = -2;

	private static Boolean[][] defaultPermissions;
	private static ThreadLocal<Boolean[][]> userDefinedPermissions = ThreadLocal.withInitial(null);

	private static Map<String, BasePermission> permissionsClasses = new HashMap<>();

	static boolean isAllowed(Permission permission) {
		
		BasePermission perm = permissionsClasses.get(permission.getClass().getName());
		
		if(perm == null) {
			//No permission set exists for this class
			return true;
		}
		
		String context = null;
		List<String> parts = Splitter.on(Permissions.DOT_PATTERN).limit(2).splitToList(permission.getName());
		
		if (parts.size() > 1) {
			context = parts.get(1);
		}
		
		Short index = perm.getIndex(permission.getName(), permission.getActions(), context);
		
		// return if index is neither accept or deny
		if(index == DENY) {
			return false;
		} else if(index == ALLOW) {
			return true;
		}		
		
		// Check if there are any user defined permissions on this
		
		Boolean[][] permissions = Permissions.userDefinedPermissions.get();
		
		if(permissions == null) {
			// This usually happen when apps directly create threads directly
			// without use the ThreadSecurity class
			initPermissions();
		}
		
		Boolean allowed = permissions[perm.getBaseIndex()][index];
		
		if(allowed != null) {
			return allowed;
		}
		
		return defaultPermissions[perm.getBaseIndex()][index];
	}

	@BlockerTodo
	static void initPermissions() {

		// Initialize userDefinedPermissions

		Boolean[][] permissions = new Boolean[defaultPermissions.length][];

		for (int i = 0; i < permissions.length; i++) {
			permissions[i] = new Boolean[PERMISSIONS_ARRAY_SIZE];
		}

		userDefinedPermissions.set(permissions);

		AppClassLoader cl = (AppClassLoader) Thread.currentThread().getContextClassLoader();
		String appId = cl.getAppId();

		// TODO Based on appId, fetch user-defined permissions and set on
		// <userDefinedPermissions>

		registerThread(Thread.currentThread(), appId);
	}

	@BlockerTodo()
	static void updatePermissions(Integer pIndex, Integer index, Boolean b) {

		// Store on permanent storage

		// Update across all threads for the current application
		// using the following: Note this one only updates the current thread

		// ---- for each thread in app
		userDefinedPermissions.get()[pIndex][index] = b;
		// ---- end for each thread in app
	}

	/**
	 * Associate a thread with a particular appId.
	 * <br>
	 * <b>Implementation Notes:</b>
	 * <br>
	 * The platform needs to maintain thread references for each application. Please
	 * find a way to map threads to their relevant app ids. You can also start a job
	 * that patrols the collection inorder to find threads that are dead. e.g using
	 * Thread.isAlive(), to know whether to remove. This will help me to achieve
	 * updatePermissions(...)
	 * 
	 * @param t
	 * @param appId
	 */
	@BlockerTodo
	private static void registerThread(Thread t, String appId) {

	}

	public static void scanPermissions() {

		final List<Integer> pIndexes = new ArrayList<>();

		new ClasspathScanner<>("Permissions", BasePermission.class).scanClasses().forEach(c -> {

			BasePermission o = ClassUtils.createInstance(c);

			if (o.getBaseIndex() < 0 || pIndexes.contains(o.getBaseIndex())) {
				Exceptions.throwRuntime("Permissions base index: " + o.getBaseIndex() + " in not valid");
			}

			LOG.debug("Registering permission set for: " + o.permissionClass());
			permissionsClasses.put(o.permissionClass(), o);

			pIndexes.add(o.getBaseIndex());
		});

		// We are ensuring that base indexes are optimally utilized

		if (!Utils.isProgressive(pIndexes)) {
			Exceptions.throwRuntime("Base indexes should be progressively defined");
		}

		// Created defaultPermissions array

		Integer maxPIndex = pIndexes.stream().mapToInt(v -> v).max().orElseThrow(NoSuchElementException::new);
		;

		defaultPermissions = new Boolean[maxPIndex + 1][];

		// Add default permissions

		for (BasePermission perm : permissionsClasses.values()) {

			Boolean[] permissions = new Boolean[PERMISSIONS_ARRAY_SIZE];

			perm.addDefaults(permissions);

			defaultPermissions[perm.getBaseIndex()] = permissions;
		}
	}

}
