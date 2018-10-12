package com.re.paas.internal.security;

import java.lang.reflect.ReflectPermission;
import java.security.Permission;
import java.security.SecurityPermission;
import java.util.List;
import java.util.PropertyPermission;
import java.util.regex.Pattern;

import javax.management.MBeanPermission;
import javax.management.MBeanServerPermission;

import com.google.common.base.Splitter;
import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.internal.security.permissions.property.PropertyPermissionsIndexes;
import com.re.paas.internal.security.permissions.runtime.RuntimePermissionIndexes;

@BlockerTodo("Add support for FilePermission, to avoid accessing protected paths: i.e /platform/internal")
public class PermissionsRepository {

	private static final Pattern UNDERSCORE_PATTERN = Pattern.compile("_");
	private static final Pattern DOT_PATTERN = Pattern.compile(Pattern.quote("."));

	private static final String RUNTIME_PERMISSION = "rt";
	private static final String SECURITY_PERMISSION = "s";
	private static final String REFLECT_PERMISSION = "rf";

	private static final String PROPERTY_PERMISSION = "p";
	private static final String MBEAN_PERMISSION = "mb";
	private static final String MBEAN_SERVER_PERMISSION = "mbs";

	static String[] permisionsList = new String[] {};

	static int getPermissionIndex(Permission permission) {

		int index = Permissions.ALLOW;
		String permissionString = toString(permission);

		if (permissionString.isEmpty()) {
			return index;
		}

		List<String> pList = Splitter.on(UNDERSCORE_PATTERN).limit(3).splitToList(permissionString);

		String type = pList.get(0);
		String alias = pList.get(1);

		switch (type) {

		case RUNTIME_PERMISSION:
			String context = pList.get(2);
			index = RuntimePermissionIndexes.getIndex(alias, context);
			break;

		case PROPERTY_PERMISSION:
			index = PropertyPermissionsIndexes.getIndex(alias);
			break;

		}

		return index;
	}

	static boolean getDefault(String permissionString) {

		return false;
	}

	static String toString(Permission permission) {

		if (permission instanceof RuntimePermission) {
			return toString(RUNTIME_PERMISSION, permission.getName(), true);
		} else if (permission instanceof SecurityPermission) {
			return toString(SECURITY_PERMISSION, permission.getName(), true);
		} else if (permission instanceof ReflectPermission) {
			return toString(REFLECT_PERMISSION, permission.getName(), true);
		} else if (permission instanceof PropertyPermission) {
			return toString(PROPERTY_PERMISSION, permission.getName(), false);
		} else if (permission instanceof MBeanPermission) {
			return toString(MBEAN_PERMISSION, permission.getActions(), true);
		} else if (permission instanceof MBeanServerPermission) {
			return toString(MBEAN_SERVER_PERMISSION, permission.getName(), true);
		}

		return null;
	}

	private static String toString(String type, String alias, boolean hasContext) {

		StringBuilder sb = new StringBuilder()

				.append(type).append(UNDERSCORE_PATTERN.pattern());

		if (hasContext) {
			List<String> parts = Splitter.on(DOT_PATTERN).limit(2).splitToList(alias);
			sb.append(parts.get(0));

			if (parts.size() > 1) {
				sb.append(UNDERSCORE_PATTERN.pattern()).append(parts.get(1));
			}

		} else {
			sb.append(alias);
		}

		return sb.toString();
	}

}
