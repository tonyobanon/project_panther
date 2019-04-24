package com.re.paas.api.realms;

import com.re.paas.api.annotations.develop.Prototype;

public abstract class PassiveRealm extends Realm {

	@Override
	public RealmApplicationSpec applicationSpec() {
		return null;
	}
	
	@Override
	@Prototype
	public Integer authority() {
		return null;
	}
	
	@Override
	public boolean passive() {
		return true;
	}
	
}
