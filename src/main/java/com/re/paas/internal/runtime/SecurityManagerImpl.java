package com.re.paas.internal.runtime;

import java.security.Permission;

public class SecurityManagerImpl extends SecurityManager {

	@Override
	public void checkPermission(Permission perm) {

		boolean allowed = Permissions.isAllowed(perm);
		
		if(!allowed) {
			throw new SecurityException("Permission was refused: " + perm.toString());
		}
		
		super.checkPermission(perm);
	}
}
