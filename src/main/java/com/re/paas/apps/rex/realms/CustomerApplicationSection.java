package com.re.paas.apps.rex.realms;

import com.re.paas.api.forms.SectionReference;
import com.re.paas.api.realms.Realm;

public enum CustomerApplicationSection implements SectionReference {

	PROFILE_INFORMATION(), CONTACT_INFORMATION();

	private Realm realm;
	private boolean importFields;

	private CustomerApplicationSection() {
		this(new CustomerRealm());
	}

	private CustomerApplicationSection(Realm realm) {
		this.realm = realm;
	}
	
	public CustomerApplicationSection setRealm(Realm realm) {
		this.realm = realm;
		return this;
	}

	@Override
	public String value() {
		return this.name();
	}

	@Override
	public Realm realm() {
		return realm;
	}
	
	public CustomerApplicationSection setImportFields(boolean importFields) {
		this.importFields = importFields;
		return this;
	}
	
	@Override
	public boolean importFields() {
		return importFields;
	}
}
