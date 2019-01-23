package com.re.paas.api.realms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.re.paas.api.forms.Reference;
import com.re.paas.api.forms.Section;
import com.re.paas.api.forms.SectionReference;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.spi.SpiDelegate;

public abstract class AbstractRealmDelegate extends SpiDelegate<Realm> {

	public abstract Collection<String> getRealmNames();

	public abstract Realm getRealm(String name);

	public abstract Collection<Functionality> getFunctionalities(Realm realm);

	public abstract Map<String, Collection<String>> getAllFunctionalities();

	public abstract Collection<Section> getSections(Realm realm);

	public abstract String getReferenceId(Realm realm, SectionReference sReference, Reference reference);

	public abstract void addRoleFunctionalities(String role, Collection<Functionality> functionalities);

	public abstract void removeRoleFunctionalities(String role, Collection<Functionality> functionalities);

	public abstract Collection<Functionality> getRoleFunctionalities(String role);

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
