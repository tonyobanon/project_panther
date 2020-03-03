package com.re.paas.api;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.api.runtime.spi.AppProvisioner;
import com.re.paas.api.utils.Utils;

@BlockerTodo("Implement Platform.State")
public class Platform {

	private static final String PLATFORM_PACKAGE = "com.re.paas";

	private static final String API_PACKAGE = PLATFORM_PACKAGE + ".api";
	private static final String INTERNAL_PACKAGE = PLATFORM_PACKAGE + ".internal";

	private static final String[] ACCESS_FORBIDDEN_PACKAGES = new String[] { "sun", "com.sun", "jdk.internal" };
	private static final String[] DEFINE_FORBIDDEN_PACKAGES = new String[] { "java", "javax", "jdk", API_PACKAGE,
			INTERNAL_PACKAGE };

	private static final String[] JVM_PACKAGES = new String[] { "sun", "com.sun", "jdk.internal", "java", "javax",
			"jdk" };

	private static boolean IS_SAFE_MODE;
	private static boolean IS_ADVANCED_MODE;

	private static boolean IS_DEV_MODE = true;

	private static Map<String, State> appStates = new HashMap<>();
	private static State state;

	@SecureMethod
	public static State getState() {
		return state;
	}

	@SecureMethod
	public static State getState(String appId) {

		if (appId == null || appId.equals(AppProvisioner.DEFAULT_APP_ID)) {
			return state;
		}

		return appStates.get(appId);
	}

	@SecureMethod
	public static void setState(State state) {
		Platform.state = state;
	}

	@SecureMethod
	public static void setState(String appId, State state) {

		if (appId == null || appId.equals(AppProvisioner.DEFAULT_APP_ID)) {
			Platform.state = state;
		}

		appStates.put(appId, state);
	}

	public static String getPlatformPackage() {
		return PLATFORM_PACKAGE;
	}

	public static void readFlags(String[] args) {

		Map<String, Boolean> jvmFlags = Utils.getFlags(args);

		if (jvmFlags.get("safe") != null && jvmFlags.get("safe").booleanValue() == true) {
			IS_SAFE_MODE = true;
		}

		if (jvmFlags.get("advanced") != null && jvmFlags.get("advanced").booleanValue() == true) {
			IS_ADVANCED_MODE = true;
		}

		if (jvmFlags.get("production") != null && jvmFlags.get("production").booleanValue() == true) {
			IS_DEV_MODE = false;
		}
	}

	public static boolean isDevMode() {
		return IS_DEV_MODE;
	}

	public static boolean isSafeMode() {
		return IS_SAFE_MODE;
	}

	public static boolean isAdvancedMode() {
		return IS_ADVANCED_MODE;
	}

	public static boolean isProduction() {
		String env = System.getenv("REALIGNITE_ENVIRONMENT");
		return env == null || !env.equals("development");
	}

	public static final String getPlatformPrefix() {
		return "realignite";
	}

	public static String getNodePrefix() {
		return getPlatformPrefix() + "-node";
	}

	public static String getPlatformName() {
		return "Real Ignite SaaS Solution";
	}

	public static Path getBaseDir() {
		return Paths.get(File.separator, getPlatformPrefix());
	}

	public static String[] getAccessForbiddenClasses() {
		return new String[] {

				// Reason: This class uses the platform's local file system. As an alternative,
				// the developer should use FileSystemProvider. Also for file creation,
				// newByteChannel(..) should be used

				"java.io.File" };
	}

	public static String[] getAccessForbiddenPackages() {
		return ACCESS_FORBIDDEN_PACKAGES;
	}

	public static String[] getDefineForbiddenPackages() {
		return DEFINE_FORBIDDEN_PACKAGES;
	}

	public static String[] getJvmPackages() {
		return JVM_PACKAGES;
	}

	public static enum State {

		STARTING, STOPPING, INSTALLING, UNINSTALLING, RUNNING;

		private Context context;

		private State() {
			this(null);
		}

		private State(Context context) {
			this.context = context;
		}

		public Context getContext() {
			return context;
		}

		public State setContext(Context context) {
			this.context = context;
			return this;
		}

		public static enum Context {

			APPLICATION, NODE, CLUSTER;

			private String appId;

			public String getAppId() {
				return appId;
			}

			public Context setAppId(String appId) {
				this.appId = appId;
				return this;
			}

		}

	}

}
