package com.re.paas.api.runtime.spi;

public enum ShutdownPhase {
	/**
	 * The delegate is being stopped either the platform or the app is being stopped
	 */
	STOP,

	/**
	 * The delegate is being taken out of service because it's parent app is being
	 * uninstalled or another app is replacing it's functionality
	 */
	OUT_OF_SERVICE
}
