package com.re.paas.api.forms;

import com.re.paas.api.realms.Realm;
import com.re.paas.api.utils.ClassUtils;

public final class SectionReference extends Reference {

	private final Realm realm;

	private SectionReference(Realm realm, String value) {
		super(value);
		this.realm = realm;
	}

	public static SectionReference create(Realm realm, String value) {
		return new SectionReference(realm, value);
	}

	public final Realm realm() {
		return this.realm;
	}

	@Override
	public String asString() {
		return realm() != null ? ClassUtils.toString(realm().getClass()) : "" + "/" + value() != null ? value() : "";
	}
}
