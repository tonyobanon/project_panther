package com.re.paas.api.realms;

public interface PassiveRealm extends Realm {

	@Override
	default RealmApplicationSpec applicationSpec() {
		return null;
	}
	
	@Override
	default Integer authority() {
		return null;
	}
	
	@Override
	default boolean passive() {
		return true;
	}
	
}
