package com.re.paas.integrated.listable;

import com.re.paas.api.classes.ClientRBRef;

public class AdminApplicationsIndex extends AbstractApplicationsIndex {

	@Override
	public String namespace() {
		return "application";
	}

	@Override
	public String id() {
		return "admin";
	}

	@Override
	public ClientSearchSpec clientSearchSpec() {
		return new ClientSearchSpec().setName(ClientRBRef.get("admin_applications"))
				.setListingPageUrl("/admin-application-search");
	}
}