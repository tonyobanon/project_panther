package com.re.paas.internal.security;

public interface BasePermission {

	String permissionClass();

	Integer getBaseIndex();

	Short getIndex(String name, String actions, String context);

	void addDefaults(Boolean[] destination);
}
