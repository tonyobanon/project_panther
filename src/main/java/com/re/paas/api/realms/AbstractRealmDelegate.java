package com.re.paas.api.realms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.re.paas.api.classes.ModifyType;
import com.re.paas.api.fusion.functionalities.Functionality;
import com.re.paas.api.runtime.spi.SpiDelegate;

public abstract class AbstractRealmDelegate extends SpiDelegate<Realm> {

	public abstract Collection<String> getRealmNames();

	/**
	 * Note: In the return realm, all functionalities has a {@link ModifyType} of
	 * null.
	 * 
	 * @param name
	 * @return
	 */
	public abstract Realm getRealm(String name);

	public abstract List<Functionality> getFunctionalities(Realm realm);

	public abstract void addRoleFunctionalities(String role, Collection<Functionality> functionalities);

	public abstract void removeRoleFunctionalities(String role, Collection<Functionality> functionalities);

	public abstract void removeRoleFunctionalities(String role);

	public abstract Collection<Functionality> getRoleFunctionalities(String role);

	public abstract void addRoleRealm(String role, Realm realm);

	public abstract void removeRoleRealm(String role);

	public Collection<String> getRoleFunctionalitiesAstring(String role) {

		Collection<Functionality> functionalities = getRoleFunctionalities(role);

		if (functionalities == null) {
			return null;
		}
		List<String> result = new ArrayList<>(functionalities.size());

		functionalities.forEach(f -> {
			result.add(f.asString());
		});

		return result;
	}

}
