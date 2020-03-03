package com.re.paas.integrated.realms;

import com.re.paas.api.forms.Section;
import com.re.paas.api.forms.SectionReference;
import com.re.paas.api.fusion.functionalities.Functionality;
import com.re.paas.api.realms.PassiveRealm;
import com.re.paas.integrated.fusion.functionalities.UserFunctionalities;

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
