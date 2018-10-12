package com.re.paas.internal.sentences;

import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.sentences.SubjectType;
import com.re.paas.internal.fusion.functionalities.UserFunctionalities;

public enum SubjectTypes implements SubjectType {

	USER(UserFunctionalities.GET_USER_PROFILE);

	private final Functionality functionality;
	
	private SubjectTypes(Functionality functionality) {
		this.functionality = functionality;
	}

	public Functionality getFunctionality() {
		return functionality;
	}
}
