package com.re.paas.api.realms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.forms.Section;
import com.re.paas.api.fusion.services.Functionality;

public interface Realm {

	public static AbstractRealmDelegate getDelegate() {
		return Singleton.get(AbstractRealmDelegate.class);
	}

	public static Realm get(String name) {
		return getDelegate().getRealm(name);
	}

	String name();

	default boolean enabled() {
		return true;
	}


	/**
	 * Check if c directly implements Realm.class
	 * 
	 * @param c
	 * @return
	 */
	static boolean isBaseRealm(Realm realm) {
		return Arrays.asList(realm.getClass().getInterfaces()).contains(Realm.class);
	}

	Functionality[] functionalities();

	Section[] onboardingForm();

	Integer authority();

	default Map<String, String> getSuggestedProfiles(Long principal, Long userId) {
		return new HashMap<>();
	}

	RealmApplicationSpec applicationSpec();
}
