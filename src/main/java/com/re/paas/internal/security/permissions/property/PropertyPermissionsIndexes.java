package com.re.paas.internal.security.permissions.property;

import com.re.paas.api.threadsecurity.SystemProperties;
import com.re.paas.internal.security.Permissions;

public class PropertyPermissionsIndexes {

	public static int getIndex(String alias) {
		return alias.startsWith(SystemProperties.prefix()) ? Permissions.ALLOW : Permissions.DENY;
	}
}
