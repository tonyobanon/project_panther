package com.re.paas.apps.shared;

import com.re.paas.api.forms.Section;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.realms.PassiveRealm;
import com.re.paas.api.realms.RealmSpec;
import com.re.paas.internal.fusion.functionalities.RoleFunctionalities;
import com.re.paas.internal.fusion.functionalities.SearchFunctionalities;
import com.re.paas.internal.fusion.functionalities.UserFunctionalities;
import com.re.paas.internal.utils.ObjectUtils;

@RealmSpec(parent = UserRealm.class)
public class PrincipalRealm implements PassiveRealm {

	@Override
	public String name() {
		return "principal";
	}

	@Override
	public Functionality[] functionalities() {

		return ObjectUtils.toArray(
				
				Functionalities.get(UserRealm.class),

				new Functionality[] { SearchFunctionalities.GET_SEARCHABLE_LISTS, UserFunctionalities.GET_PERSON_NAMES,
						RoleFunctionalities.GET_REALM_FUNCTIONALITIES, RoleFunctionalities.GET_ROLE_FUNCTIONALITIES,
						UserFunctionalities.GET_USER_PROFILE });
	}

	@Override
	public Section[] onboardingForm() {
		// TODO Auto-generated method stub
		return null;
	}

}
