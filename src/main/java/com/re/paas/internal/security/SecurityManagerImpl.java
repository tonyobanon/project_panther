package com.re.paas.internal.security;

import java.security.Permission;

import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.threadsecurity.ThreadSecurity;

public class SecurityManagerImpl extends SecurityManager {

	
	@Override
	public void checkPermission(Permission perm) {

		if(ThreadSecurity.get().isTrusted()) {
			super.checkPermission(perm);
			return;
		}
		
		boolean allowed = Permissions.isAllowed(perm);
		
		if(!allowed) {
			throw new SecurityException("Permission was refused: " + perm.toString());
		}
		
		super.checkPermission(perm);
	}
}
