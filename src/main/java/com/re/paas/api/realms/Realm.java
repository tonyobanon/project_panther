package com.re.paas.api.realms;

import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.forms.Section;
import com.re.paas.api.fusion.functionalities.RealmFunctionality;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.SpiType;

public abstract class Realm extends AbstractResource {

	public Realm() {
		super(SpiType.REALM);
	}

	public static AbstractRealmDelegate getDelegate() {
		return Singleton.get(AbstractRealmDelegate.class);
	}

	public static Realm get(String name) {
		return getDelegate().getRealm(name);
	}

	public abstract String name();

	public abstract RealmFunctionality[] functionalities();

	public abstract Section[] onboardingForm();

	public Integer authority() {
		return 0;
	}

	public abstract RealmApplicationSpec applicationSpec();

	@Override
	public final String toString() {
		return name();
	}
}
