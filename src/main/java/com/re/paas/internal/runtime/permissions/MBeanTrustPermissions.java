package com.re.paas.internal.runtime.permissions;


import javax.management.MBeanTrustPermission;

import com.re.paas.internal.runtime.Permissions;

public class MBeanTrustPermissions implements BasePermission {

	@Override
	public String permissionClass() {
		return MBeanTrustPermission.class.getName();
	}

	@Override
	public Integer getBaseIndex() {
		return 2;
	}

	@Override
	public Short getIndex(String name, String actions, String context) {
		return Permissions.ALLOW;
	}

	@Override
	public void addDefaults(Boolean[] destination) {
	}
}
