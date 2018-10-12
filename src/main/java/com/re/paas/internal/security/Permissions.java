package com.re.paas.internal.security;

import java.security.Permission;
import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.internal.security.permissions.runtime.RuntimePermissionIndexes;

public class Permissions {
	
	private static final int PERMISSIONS_LENGTH = 100;
	
	public static final int ALLOW = -1;
	public static final int DENY = -2;
	
	private static final Map<String, Boolean[]> appPermissions = new HashMap<>();
	private static ThreadLocal<Boolean[]> permissions = ThreadLocal.withInitial(null);
	
	static boolean isAllowed(Permission permission) {
		
		Boolean[] permissions = Permissions.permissions.get();
		
		if(permissions == null) {
			initPermissions();
		}
		
		int index = PermissionsRepository.getPermissionIndex(permission);
		
		if(index == DENY) {
			return false;
		} else if(index == ALLOW || permissions[index]) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * This instantiates required permissions for the current thread
	 */
	static void initPermissions() {
		
		AppClassLoader cl = (AppClassLoader) Thread.currentThread().getContextClassLoader();
		String appId = cl.getAppId();
		
		Boolean[] permissions = appPermissions.get(appId);
		
		if(permissions == null) {
			
			permissions = new Boolean[PERMISSIONS_LENGTH];
			
			RuntimePermissionIndexes.addDefaults(permissions);
			// Add other defaults
			
			appPermissions.put(appId, permissions);
		}
		

		Permissions.permissions.set(permissions);
	}

	

}
