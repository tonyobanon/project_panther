package com.re.paas.integrated.listable;

import com.re.paas.api.classes.ClientRBRef;

public class UserApplicationsIndex extends AbstractApplicationsIndex {

	@Override
	public String namespace() {
		return "application";
	}

	@Override
	public String id() {
		return "user";
	}

	@Override
	public ClientSearchSpec clientSearchSpec() {
		return new ClientSearchSpec().setName(ClientRBRef.get("user_applications"))
				.setListingPageUrl("/user-application-search");
	}
}