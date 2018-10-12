package com.re.paas.internal.security.permissions.runtime;

public class RuntimePermissionNames {

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
