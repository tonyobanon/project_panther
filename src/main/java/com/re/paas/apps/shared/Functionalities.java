package com.re.paas.apps.shared;

import java.util.Collection;

import com.google.common.collect.Lists;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.realms.AbstractRealmDelegate;
import com.re.paas.api.realms.Realm;
import com.re.paas.api.utils.ClassUtils;

public class Functionalities {

	public static Functionality[] get(Class<? extends Realm> realmClass) {
		
		Realm realm = ClassUtils.createInstance(realmClass);
		AbstractRealmDelegate delegate = Realm.getDelegate();
		
		Collection<Functionality> functionalities = delegate.getFunctionalities(realm);
		return Lists.newArrayList(functionalities).toArray(new Functionality[functionalities.size()]);
	}
}
