package com.re.paas.internal.realms;

import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.internal.fusion.functionalities.RoleFunctionalities;
import com.re.paas.internal.fusion.functionalities.SearchFunctionalities;
import com.re.paas.internal.fusion.functionalities.UserFunctionalities;

public class PrincipalRealm {
	
	public Functionality[] functionalities() {
		return new Functionality[] { 
				SearchFunctionalities.GET_SEARCHABLE_LISTS, 
				UserFunctionalities.GET_PERSON_NAMES,
				RoleFunctionalities.GET_REALM_FUNCTIONALITIES, 
				RoleFunctionalities.GET_ROLE_FUNCTIONALITIES,
				UserFunctionalities.GET_USER_PROFILE };
	}
	
}
