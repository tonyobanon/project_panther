package com.re.paas.apps.shared;

import com.re.paas.api.forms.Section;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.realms.PassiveRealm;
import com.re.paas.internal.fusion.functionalities.UserFunctionalities;

public class UserRealm implements PassiveRealm {

	@Override
	public String name() {
		return "user";
	}

	@Override
	public Functionality[] functionalities() {
		return new Functionality[] {
				UserFunctionalities.VIEW_OWN_PROFILE,
				UserFunctionalities.MANAGE_OWN_PROFILE 
		};
	}

	@Override
	public Section[] onboardingForm() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
