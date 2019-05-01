package com.re.paas.internal.runtime.permissions;

import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.app_provisioning.AppClassLoader.DelegationType;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.Platform;
import com.re.paas.internal.runtime.Permissions;

public class RuntimePermissions implements BasePermission {

	@Override
	public String permissionClass() {
		return RuntimePermission.class.getName();
	}

	@Override
	public Integer getBaseIndex() {
		return 0;
	}

	public Short getIndex(String name, String actions, String context) {

		switch (name) {

		case PermissionNames.CREATE_CLASS_LOADER:
			return PermissionIndexes.CREATE_CLASS_LOADER;
		case PermissionNames.GET_CLASS_LOADER:
			return PermissionIndexes.GET_CLASS_LOADER;
		case PermissionNames.SET_CONTEXT_CLASS_LOADER:
			return PermissionIndexes.SET_CONTEXT_CLASS_LOADER;
		case PermissionNames.ENABLE_CONTEXT_CLASS_LOADER_OVERRIDE:
			return PermissionIndexes.ENABLE_CONTEXT_CLASS_LOADER_OVERRIDE;
		case PermissionNames.CLOSE_CLASS_LOADER:
			return PermissionIndexes.CLOSE_CLASS_LOADER;
		case PermissionNames.SET_SECURITY_MANAGER:
			return PermissionIndexes.SET_SECURITY_MANAGER;
		case PermissionNames.CREATE_SECURITY_MANAGER:
			return PermissionIndexes.CREATE_SECURITY_MANAGER;
		case PermissionNames.GET_ENV:
			return PermissionIndexes.GET_ENV;
		case PermissionNames.EXIT_VM:
			return PermissionIndexes.EXIT_VM;
		case PermissionNames.SHUTDOWN_HOOKS:
			return PermissionIndexes.SHUTDOWN_HOOKS;
		case PermissionNames.SET_FACTORY:
			return PermissionIndexes.SET_FACTORY;
		case PermissionNames.SET_IO:
			return PermissionIndexes.SET_IO;
		case PermissionNames.MODIFY_THREAD:
			return PermissionIndexes.MODIFY_THREAD;
		case PermissionNames.STOP_THREAD:
			return PermissionIndexes.STOP_THREAD;

		case PermissionNames.MODIFY_THREAD_GROUP:

			
			/**
			 * No execution context is permitted to modify it's thread group, and this
			 * includes directly creating new threads. The helper method should be used
			 * instead
			 */
			return Permissions.DENY;

		case PermissionNames.GET_PROTECTION_DOMAIN:
			return PermissionIndexes.GET_PROTECTION_DOMAIN;
		case PermissionNames.GET_FILE_SYSTEM_ATTRIBUTES:
			return PermissionIndexes.GET_FILE_SYSTEM_ATTRIBUTES;
		case PermissionNames.READ_FILE_DESCRIPTOR:
			return PermissionIndexes.READ_FILE_DESCRIPTOR;
		case PermissionNames.WRITE_FILE_DESCRIPTOR:
			return PermissionIndexes.WRITE_FILE_DESCRIPTOR;
		case PermissionNames.LOAD_LIBRARY:
			return PermissionIndexes.LOAD_LIBRARY;

		case PermissionNames.ACCESS_CLASS_IN_PACAKAGE:

			if (Utils.startsWith(context, Platform.getAccessForbiddenPackages())) {
				return Permissions.DENY;
			}

			if (Utils.equals(context, Platform.getAccessForbiddenClasses())) {
				return Permissions.DENY;
			}

			return PermissionIndexes.ACCESS_CLASS_IN_PACAKAGE;

		case PermissionNames.DEFINE_CLASS_IN_PACKAGE:

			if (AppClassLoader.getDelegationType(context) == DelegationType.FIND_FIRST) {

				if (Utils.startsWith(context, Platform.getDefineForbiddenPackages())) {
					return Permissions.DENY;
				}
			} else {
				// There is no security risk, since AppClassLoader eagerly delegates to
				// the parent classloader
			}

			return PermissionIndexes.DEFINE_CLASS_IN_PACKAGE;

		case PermissionNames.ACCESS_DECLARED_MEMNERS:
			return PermissionIndexes.ACCESS_DECLARED_MEMNERS;
		case PermissionNames.QUEUE_PRINT_JOB:
			return PermissionIndexes.QUEUE_PRINT_JOB;
		case PermissionNames.GET_STACK_TRACE:
			return PermissionIndexes.GET_STACK_TRACE;
		case PermissionNames.SET_DEFAULT_UNCAUGHT_EXCEPTION:
			return PermissionIndexes.SET_DEFAULT_UNCAUGHT_EXCEPTION;
		case PermissionNames.PREFERENCES:
			return PermissionIndexes.PREFERENCES;
		case PermissionNames.USE_POLICY:
			return PermissionIndexes.USE_POLICY;
		}

		return Permissions.ALLOW;
	}

	public void addDefaults(Boolean[] destination) {

		destination[PermissionIndexes.CREATE_CLASS_LOADER] = false;
		destination[PermissionIndexes.GET_CLASS_LOADER] = false;
		destination[PermissionIndexes.SET_CONTEXT_CLASS_LOADER] = false;
		destination[PermissionIndexes.ENABLE_CONTEXT_CLASS_LOADER_OVERRIDE] = false;
		destination[PermissionIndexes.CLOSE_CLASS_LOADER] = false;
		destination[PermissionIndexes.SET_SECURITY_MANAGER] = false;
		destination[PermissionIndexes.CREATE_SECURITY_MANAGER] = false;

		destination[PermissionIndexes.GET_ENV] = false;

		destination[PermissionIndexes.EXIT_VM] = false;

		destination[PermissionIndexes.SHUTDOWN_HOOKS] = false;
		destination[PermissionIndexes.SET_FACTORY] = false;
		destination[PermissionIndexes.SET_IO] = false;

		destination[PermissionIndexes.MODIFY_THREAD] = true;
		destination[PermissionIndexes.STOP_THREAD] = false;
		destination[PermissionIndexes.MODIFY_THREAD_GROUP] = false;
		destination[PermissionIndexes.GET_PROTECTION_DOMAIN] = false;
		destination[PermissionIndexes.GET_FILE_SYSTEM_ATTRIBUTES] = false;
		destination[PermissionIndexes.READ_FILE_DESCRIPTOR] = false;
		destination[PermissionIndexes.WRITE_FILE_DESCRIPTOR] = false;

		destination[PermissionIndexes.LOAD_LIBRARY] = false;
		destination[PermissionIndexes.ACCESS_CLASS_IN_PACAKAGE] = true;
		destination[PermissionIndexes.DEFINE_CLASS_IN_PACKAGE] = true;

		destination[PermissionIndexes.ACCESS_DECLARED_MEMNERS] = false;

		destination[PermissionIndexes.QUEUE_PRINT_JOB] = false;
		destination[PermissionIndexes.GET_STACK_TRACE] = false;
		destination[PermissionIndexes.SET_DEFAULT_UNCAUGHT_EXCEPTION] = false;
		destination[PermissionIndexes.PREFERENCES] = false;
		destination[PermissionIndexes.USE_POLICY] = false;
	}

	private static class PermissionIndexes {

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
	}

	private static class PermissionNames {

		public static final String CREATE_CLASS_LOADER = "createClassLoader";
		public static final String GET_CLASS_LOADER = "getClassLoader";
		public static final String SET_CONTEXT_CLASS_LOADER = "setContextClassLoader";
		public static final String ENABLE_CONTEXT_CLASS_LOADER_OVERRIDE = "enableContextClassLoaderOverride";
		public static final String CLOSE_CLASS_LOADER = "closeClassLoader";
		public static final String SET_SECURITY_MANAGER = "setSecurityManager";
		public static final String CREATE_SECURITY_MANAGER = "createSecurityManager";

		public static final String GET_ENV = "getenv";

		public static final String EXIT_VM = "exitVM";

		public static final String SHUTDOWN_HOOKS = "shutdownHooks";
		public static final String SET_FACTORY = "setFactory";
		public static final String SET_IO = "setIO";

		public static final String MODIFY_THREAD = "modifyThread";
		public static final String STOP_THREAD = "stopThread";
		public static final String MODIFY_THREAD_GROUP = "modifyThreadGroup";
		public static final String GET_PROTECTION_DOMAIN = "getProtectionDomain";
		public static final String GET_FILE_SYSTEM_ATTRIBUTES = "getFileSystemAttributes";
		public static final String READ_FILE_DESCRIPTOR = "readFileDescriptor";
		public static final String WRITE_FILE_DESCRIPTOR = "writeFileDescriptor";

		public static final String LOAD_LIBRARY = "loadLibrary";
		public static final String ACCESS_CLASS_IN_PACAKAGE = "accessClassInPackage";
		public static final String DEFINE_CLASS_IN_PACKAGE = "defineClassInPackage";

		public static final String ACCESS_DECLARED_MEMNERS = "accessDeclaredMembers";

		public static final String QUEUE_PRINT_JOB = "queuePrintJob";
		public static final String GET_STACK_TRACE = "getStackTrace";
		public static final String SET_DEFAULT_UNCAUGHT_EXCEPTION = "setDefaultUncaughtExceptionHandler";
		public static final String PREFERENCES = "preferences";
		public static final String USE_POLICY = "usePolicy";

	}

}
