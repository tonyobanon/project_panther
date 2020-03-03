package com.re.paas.internal.runtime.permissions;

import java.util.PropertyPermission;

import com.re.paas.api.runtime.RuntimeIdentity;
import com.re.paas.internal.runtime.Permissions;

public class PropertyPermissions implements BasePermission {

	@Override
	public String permissionClass() {
		return PropertyPermission.class.getName();
	}

	@Override
	public Integer getBaseIndex() {
		return 1;
	}

	@Override
	public Short getIndex(String name, String actions, String context) {

		// Use indexes

		if (actions.equals("write")) {
			return Permissions.DENY;
		}
		;

		switch (name) {
		case "sun.reflect.debugModuleAccessChecks":
			return Permissions.ALLOW;
		case "jdk.proxy.debug":
			return Permissions.ALLOW;
		case "jdk.proxy.ProxyGenerator.saveGeneratedFiles":
			return Permissions.ALLOW;
		case "java.io.tmpdir":
			return Permissions.ALLOW;
		case "java.net.preferIPv6Addresses":
			return Permissions.ALLOW;
		case "impl.prefix":
			return Permissions.ALLOW;
		case "jdk.net.hosts.file":
			return Permissions.ALLOW;
		case "*":
			return Permissions.ALLOW;
		case "line.separator":
			return Permissions.ALLOW;
		case "tika.config":
			return Permissions.ALLOW;
		case "org.apache.tika.service.error.warn":
			return Permissions.ALLOW;
		case "jaxp.debug":
			return Permissions.ALLOW;
		case "javax.xml.parsers.SAXParserFactory":
			return Permissions.ALLOW;
		}

		return Permissions.DENY;
	}

	@Override
	public void addDefaults(Boolean[] destination) {
	}

	/**
	 * Applications that wish to store system properties should prepend this prefix
	 * to the keys
	 * 
	 * @return
	 */
	private static String prefix() {
		String appId = RuntimeIdentity.getAppId();
		return appId != null ? "app." + appId + ".props." : "";
	}
}
