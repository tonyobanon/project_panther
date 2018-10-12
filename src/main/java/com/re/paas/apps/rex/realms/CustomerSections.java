package com.re.paas.apps.rex.realms;

import com.re.paas.api.forms.SectionReference;
import com.re.paas.api.realms.Realm;

public enum CustomerSections implements SectionReference {

	PROFILE_INFORMATION(), CONTACT_INFORMATION();

	private Realm realm;
	private boolean importFields;

	private CustomerSections() {
		this(new CustomerRealm());
	}

	private CustomerSections(Realm realm) {
		this.realm = realm;
	}

	@Override
	public String value() {
		return this.name();
	}

	@Override
	public Realm realm() {
		return realm;
	}
	
	public CustomerSections setImportFields(boolean importFields) {
		this.importFields = importFields;
		return this;
	}
	
	@Override
	public boolean importFields() {
		return importFields;
	}
}
