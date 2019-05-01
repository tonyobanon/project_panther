package com.re.paas.internal.runtime.permissions;

import java.util.PropertyPermission;

import com.re.paas.api.runtime.ClassLoaderSecurity;
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
		
		if(actions.equals("write")) {
			return name.startsWith(prefix()) ? Permissions.ALLOW : Permissions.DENY;
		};
		
		switch(name) {
			case "sun.reflect.debugModuleAccessChecks":
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
	 * @return
	 */
	private static String prefix() {
		String appId = ClassLoaderSecurity.getAppId();
		return appId != null ? "app." + appId + ".props." : "";
	}
}
