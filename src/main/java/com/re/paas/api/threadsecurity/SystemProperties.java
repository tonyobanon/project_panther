package com.re.paas.api.threadsecurity;

public class SystemProperties {

	/**
	 * Applications that wish to store system properties should prepend this prefix
	 * to the keys
	 * @return
	 */
	public static String prefix() {
		String appId = ThreadSecurity.getAppId();
		return appId != null ? "app." + appId + ".props." : "";
	}

}
