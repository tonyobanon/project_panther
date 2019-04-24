package com.re.paas.internal.runtime.permissions;

import java.util.PropertyPermission;

import com.re.paas.api.runtime.ThreadSecurity;
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
		return name.startsWith(prefix()) ? Permissions.ALLOW : Permissions.DENY;
	}

	@Override
	public void addDefaults(Boolean[] destination) {
	}

	/**
	 * Applications that wish to store system properties should prepend this prefix
	 * to the keys
	 * @return
	 */
	private static String prefix() {
		String appId = ThreadSecurity.getAppId();
		return appId != null ? "app." + appId + ".props." : "";
	}
}
