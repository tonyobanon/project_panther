package com.re.paas.internal.realms;

import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.internal.fusion.functionalities.UserFunctionalities;

public class UserRealm {

	public Functionality[] functionalities() {
		return new Functionality[] {
				UserFunctionalities.VIEW_OWN_PROFILE,
				UserFunctionalities.MANAGE_OWN_PROFILE 
		};
	}
}
