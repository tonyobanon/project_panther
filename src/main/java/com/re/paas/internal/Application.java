package com.re.paas.internal;

import java.util.Map;

import com.re.paas.api.utils.Utils;

public class Application {

	public static final String APPLICATION_NAME = "Compute Essentials";

	public static final String SOFTWARE_VENDOR_NAME = "Compute Essentials, Inc";
	public static final String SOFTWARE_VENDOR_EMAIL = "corporate@compute-essentials.com";

	private static boolean isStarted = false;

	public static boolean IS_SAFE_MODE;
	public static boolean IS_ADVANCED_MODE;

	public static void main(String[] args) {

		if (isStarted) {
			return;
		}

		Map<String, Boolean> jvmFlags = Utils.getFlags(args);

		if (jvmFlags.get("safemode") != null && jvmFlags.get("safemode").booleanValue() == true) {
			IS_SAFE_MODE = true;
		}

		if (jvmFlags.get("advancedmode") != null && jvmFlags.get("advancedmode").booleanValue() == true) {
			IS_ADVANCED_MODE = true;
		}

		AppDelegate.main();

		// Add shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			stop();
		}));

		isStarted = true;
	}

	public static boolean isStarted() {
		return isStarted;
	}

	private static void stop() {
		AppDelegate.stop();
		isStarted = false;
	}

}
