package com.re.paas.integrated.realms;

import java.util.Collection;

import com.google.common.collect.Lists;
import com.re.paas.api.fusion.functionalities.Functionality;
import com.re.paas.api.realms.AbstractRealmDelegate;
import com.re.paas.api.realms.Realm;
import com.re.paas.api.utils.ClassUtils;

public class Functionalities {

	public static Functionality[] get(Class<? extends Realm> realmClass) {
		
		Realm realm = com.re.paas.internal.classes.ClassUtil.createInstance(realmClass);
		AbstractRealmDelegate delegate = Realm.getDelegate();
		
		Collection<Functionality> functionalities = delegate.getFunctionalities(realm);
		return Lists.newArrayList(functionalities).toArray(new Functionality[functionalities.size()]);
	}
}
