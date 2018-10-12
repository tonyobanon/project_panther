package com.re.paas.api.fusion.ui;

import com.re.paas.api.designpatterns.Singleton;

public abstract class AbstractComponent {

	public static AbstractUIComponentDelegate getDelegate() {
		return Singleton.get(AbstractUIComponentDelegate.class);
	}

}
