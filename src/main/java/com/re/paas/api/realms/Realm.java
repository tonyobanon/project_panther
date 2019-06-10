package com.re.paas.api.realms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.forms.Section;
import com.re.paas.api.fusion.services.Functionality;
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

	public boolean enabled() {
		return true;
	}

	public boolean passive() {
		return false;
	}

	/**
	 * Check if realm directly implements Realm.class
	 * 
	 * @param realm
	 * @return
	 */
	public static boolean isBaseRealm(Realm realm) {
		return Arrays.asList(realm.getClass().getInterfaces()).contains(Realm.class);
	}

	public abstract Functionality[] functionalities();

	public abstract Section[] onboardingForm();

	public abstract Integer authority();

	public  Map<String, String> getSuggestedProfiles(Long principal, Long userId) {
		return new HashMap<>();
	}

	public abstract RealmApplicationSpec applicationSpec();
}
