package com.re.paas.apps.rex.models.listables;

import java.util.List;

import com.re.paas.api.listable.Listable;
import com.re.paas.api.listable.ListingFilter;
import com.re.paas.api.realms.Realm;
import com.re.paas.internal.models.BaseAgentModel;
import com.re.paas.internal.models.BaseUserModel;
import com.re.paas.internal.models.RoleModel;
import com.re.paas.internal.realms.AdminRealm;

public abstract class AbstractAgentOrganizationList<T> extends Listable<T> {

	static boolean canProvisionOrganization(Long userId, List<ListingFilter> listingFilters) {

		String role = BaseUserModel.getRole(userId);
		Realm realm = RoleModel.getRealm(role);

		if (realm instanceof AdminRealm) {
			return true;
		}

		boolean canProvisionOrganization = false;

		for (ListingFilter lf : listingFilters) {

			Object agentOrganizationObj = lf.getFilters().get("agentOrganization");
			if (agentOrganizationObj == null) {
				continue;
			}

			Long agentOrganization = Long.parseLong(agentOrganizationObj.toString());
			Long principalAgentOrganization = BaseAgentModel.getAgentOrganization(realm, userId);

			canProvisionOrganization =
					// Admin
					principalAgentOrganization == null || agentOrganization.equals(principalAgentOrganization);
		}

		return canProvisionOrganization;
	}

}
