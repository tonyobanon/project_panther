package com.re.paas.internal.models.listables;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;
import java.util.Map;

import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.listable.Listable;
import com.re.paas.api.listable.ListingFilter;
import com.re.paas.api.listable.ListingType;
import com.re.paas.api.models.classes.IndexedNameSpec;
import com.re.paas.api.realms.Realm;
import com.re.paas.internal.classes.ApplicationStatus;
import com.re.paas.internal.classes.spec.BaseApplicationSpec;
import com.re.paas.internal.entites.ApplicationEntity;
import com.re.paas.internal.models.ApplicationModel;
import com.re.paas.internal.models.RoleModel;
import com.re.paas.internal.users.RoleRealm;

public abstract class AbstractApplicationsList extends Listable<BaseApplicationSpec> {

	@Override
	public Map<String, BaseApplicationSpec> getAll(List<String> keys) {

		Map<String, BaseApplicationSpec> result = new FluentHashMap<>();

		keys.forEach(k -> {

			Long applicationId = Long.parseLong(k);

			String role = ApplicationModel.getApplicationRole(applicationId);
			Realm realm = RoleModel.getRealm(role);

			IndexedNameSpec nameSpec = ApplicationModel.getNameSpec(applicationId, realm);

			ApplicationEntity e = ofy().load().type(ApplicationEntity.class).id(applicationId).safe();

			BaseApplicationSpec spec = new BaseApplicationSpec().setId(applicationId).setRole(e.getRole())
					.setStatus(ApplicationStatus.from(e.getStatus())).setNameSpec(nameSpec)
					.setDateCreated(e.getDateCreated()).setDateUpdated(e.getDateUpdated());

			result.put(k, spec);

		});

		return result;
	}
	
	@Override
	public boolean authenticate(ListingType type, Long userId, List<ListingFilter> listingFilters) {
		
		// Check spec.reviewFunctionality()
		
	}

}
