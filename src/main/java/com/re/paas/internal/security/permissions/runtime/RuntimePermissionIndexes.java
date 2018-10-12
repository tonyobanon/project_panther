package com.re.paas.internal.security.permissions.runtime;

import com.re.paas.api.annotations.Prototype;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.app_provisioning.AppClassLoader.DelegationType;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.Platform;
import com.re.paas.internal.security.Permissions;

@Prototype
public class RuntimePermissionIndexes {

	public static final int CREATE_CLASS_LOADER = 0;
	public static final int GET_CLASS_LOADER = 1;
	public static final int SET_CONTEXT_CLASS_LOADER = 3;
	public static final int ENABLE_CONTEXT_CLASS_LOADER_OVERRIDE = 4;
	public static final int CLOSE_CLASS_LOADER = 5;
	public static final int SET_SECURITY_MANAGER = 6;
	public static final int CREATE_SECURITY_MANAGER = 7;

	public static final int GET_ENV = 8;

	public static final int EXIT_VM = 9;

	public static final int SHUTDOWN_HOOKS = 10;
	public static final int SET_FACTORY = 11;
	public static final int SET_IO = 12;

	public static final int MODIFY_THREAD = 13;
	public static final int STOP_THREAD = 14;
	public static final int MODIFY_THREAD_GROUP = 15;
	public static final int GET_PROTECTION_DOMAIN = 16;
	public static final int GET_FILE_SYSTEM_ATTRIBUTES = 17;
	public static final int READ_FILE_DESCRIPTOR = 18;
	public static final int WRITE_FILE_DESCRIPTOR = 19;

	public static final int LOAD_LIBRARY = 20;
	public static final int ACCESS_CLASS_IN_PACAKAGE = 21;
	public static final int DEFINE_CLASS_IN_PACKAGE = 22;

	public static final int ACCESS_DECLARED_MEMNERS = 23;

	public static final int QUEUE_PRINT_JOB = 24;
	public static final int GET_STACK_TRACE = 25;
	public static final int SET_DEFAULT_UNCAUGHT_EXCEPTION = 26;
	public static final int PREFERENCES = 27;
	public static final int USE_POLICY = 28;

	public static int getIndex(String alias, String context) {

		switch (alias) {

		case RuntimePermissionNames.CREATE_CLASS_LOADER:
			return RuntimePermissionIndexes.CREATE_CLASS_LOADER;
		case RuntimePermissionNames.GET_CLASS_LOADER:
			return RuntimePermissionIndexes.GET_CLASS_LOADER;
		case RuntimePermissionNames.ENABLE_CONTEXT_CLASS_LOADER_OVERRIDE:
			return RuntimePermissionIndexes.ENABLE_CONTEXT_CLASS_LOADER_OVERRIDE;
		case RuntimePermissionNames.CLOSE_CLASS_LOADER:
			return RuntimePermissionIndexes.CLOSE_CLASS_LOADER;
		case RuntimePermissionNames.SET_SECURITY_MANAGER:
			return RuntimePermissionIndexes.SET_SECURITY_MANAGER;
		case RuntimePermissionNames.CREATE_SECURITY_MANAGER:
			return RuntimePermissionIndexes.CREATE_SECURITY_MANAGER;
		case RuntimePermissionNames.GET_ENV:
			return RuntimePermissionIndexes.GET_ENV;
		case RuntimePermissionNames.EXIT_VM:
			return RuntimePermissionIndexes.EXIT_VM;
		case RuntimePermissionNames.SHUTDOWN_HOOKS:
			return RuntimePermissionIndexes.SHUTDOWN_HOOKS;
		case RuntimePermissionNames.SET_FACTORY:
			return RuntimePermissionIndexes.SET_FACTORY;
		case RuntimePermissionNames.SET_IO:
			return RuntimePermissionIndexes.SET_IO;
		case RuntimePermissionNames.MODIFY_THREAD:
			return RuntimePermissionIndexes.MODIFY_THREAD;
		case RuntimePermissionNames.STOP_THREAD:
			return RuntimePermissionIndexes.STOP_THREAD;
		case RuntimePermissionNames.MODIFY_THREAD_GROUP:
			return RuntimePermissionIndexes.MODIFY_THREAD_GROUP;
		case RuntimePermissionNames.GET_PROTECTION_DOMAIN:
			return RuntimePermissionIndexes.GET_PROTECTION_DOMAIN;
		case RuntimePermissionNames.GET_FILE_SYSTEM_ATTRIBUTES:
			return RuntimePermissionIndexes.GET_FILE_SYSTEM_ATTRIBUTES;
		case RuntimePermissionNames.READ_FILE_DESCRIPTOR:
			return RuntimePermissionIndexes.READ_FILE_DESCRIPTOR;
		case RuntimePermissionNames.WRITE_FILE_DESCRIPTOR:
			return RuntimePermissionIndexes.WRITE_FILE_DESCRIPTOR;
		case RuntimePermissionNames.LOAD_LIBRARY:
			return RuntimePermissionIndexes.LOAD_LIBRARY;

		case RuntimePermissionNames.ACCESS_CLASS_IN_PACAKAGE:

			if (Utils.startsWith(context, Platform.getAccessForbiddenPackages())) {
				return Permissions.DENY;
			}

			return RuntimePermissionIndexes.ACCESS_CLASS_IN_PACAKAGE;

		case RuntimePermissionNames.DEFINE_CLASS_IN_PACKAGE:

			if (AppClassLoader.getDelegationType() == DelegationType.DELEGATE_LATER) {

				if (Utils.startsWith(context, Platform.getDefineForbiddenPackages())) {
					return Permissions.DENY;
				}
			} else {
				// There is no security risk, since AppClassLoader eagerly delegates to
				// the parent classloader
			}

			return RuntimePermissionIndexes.DEFINE_CLASS_IN_PACKAGE;

		case RuntimePermissionNames.ACCESS_DECLARED_MEMNERS:
			return RuntimePermissionIndexes.ACCESS_DECLARED_MEMNERS;
		case RuntimePermissionNames.QUEUE_PRINT_JOB:
			return RuntimePermissionIndexes.QUEUE_PRINT_JOB;
		case RuntimePermissionNames.GET_STACK_TRACE:
			return RuntimePermissionIndexes.GET_STACK_TRACE;
		case RuntimePermissionNames.SET_DEFAULT_UNCAUGHT_EXCEPTION:
			return RuntimePermissionIndexes.SET_DEFAULT_UNCAUGHT_EXCEPTION;
		case RuntimePermissionNames.PREFERENCES:
			return RuntimePermissionIndexes.PREFERENCES;
		case RuntimePermissionNames.USE_POLICY:
			return RuntimePermissionIndexes.USE_POLICY;
		}

		return Permissions.ALLOW;
	}

	public static void addDefaults(Boolean[] destination) {

		destination[CREATE_CLASS_LOADER] = false;
		destination[GET_CLASS_LOADER] = false;
		destination[SET_CONTEXT_CLASS_LOADER] = false;
		destination[ENABLE_CONTEXT_CLASS_LOADER_OVERRIDE] = false;
		destination[CLOSE_CLASS_LOADER] = false;
		destination[SET_SECURITY_MANAGER] = false;
		destination[CREATE_SECURITY_MANAGER] = false;

		destination[GET_ENV] = false;

		destination[EXIT_VM] = false;

		destination[SHUTDOWN_HOOKS] = false;
		destination[SET_FACTORY] = false;
		destination[SET_IO] = false;

		destination[MODIFY_THREAD] = true;
		destination[STOP_THREAD] = false;
		destination[MODIFY_THREAD_GROUP] = false;
		destination[GET_PROTECTION_DOMAIN] = false;
		destination[GET_FILE_SYSTEM_ATTRIBUTES] = false;
		destination[READ_FILE_DESCRIPTOR] = false;
		destination[WRITE_FILE_DESCRIPTOR] = false;

		destination[LOAD_LIBRARY] = false;
		destination[ACCESS_CLASS_IN_PACAKAGE] = true;
		destination[DEFINE_CLASS_IN_PACKAGE] = true;

		destination[ACCESS_DECLARED_MEMNERS] = false;

		destination[QUEUE_PRINT_JOB] = false;
		destination[GET_STACK_TRACE] = false;
		destination[SET_DEFAULT_UNCAUGHT_EXCEPTION] = false;
		destination[PREFERENCES] = false;
		destination[USE_POLICY] = false;

	}
}
