package com.re.paas.apps.rex.realms;

import com.re.paas.api.forms.Section;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.realms.Realm;
import com.re.paas.api.realms.RealmApplicationSpec;
import com.re.paas.apps.rex.functionality.AgentOrganizationFunctionalities;
import com.re.paas.apps.rex.functionality.PropertyFunctionalities;
import com.re.paas.apps.rex.models.listables.IndexedNameTypes;
import com.re.paas.apps.rex.sentences.ObjectTypes;
import com.re.paas.apps.shared.Functionalities;
import com.re.paas.apps.shared.UserRealm;
import com.re.paas.internal.utils.ObjectUtils;

public class CustomerRealm extends Realm {

	@Override
	public String name() {
		return "customer";
	}
	
	@Override
	public Functionality[] functionalities() {
		return ObjectUtils.toArray(
				Functionalities.get(UserRealm.class),	// Fix error and uncomment		
				new Functionality[] { PropertyFunctionalities.ADD_TO_OWN_SAVED_LIST,
						PropertyFunctionalities.REMOVE_FROM_OWN_SAVED_LIST,
						PropertyFunctionalities.GET_OWN_SAVED_LIST });
	}

	@Override
	public Section[] onboardingForm() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer authority() {
		return null;
	}

	@Override
	public RealmApplicationSpec applicationSpec() {
		return new RealmApplicationSpec()
				.setRequiresReview(false)
				.setBaseObjectType(ObjectTypes.ORGANIZATION_ADMIN_APPLICATION)
				.setReviewFunctionality(AgentOrganizationFunctionalities.REVIEW_ORGANIZATION_ADMIN_APPLICATION)
				.setIndexedNameType(IndexedNameTypes.ORGANIZATION_ADMIN_APPLICATION);
	}

}
