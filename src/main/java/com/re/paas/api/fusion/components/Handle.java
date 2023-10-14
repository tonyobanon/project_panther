package com.re.paas.api.fusion.components;

public abstract class Handle {

	private BaseComponent<?> component;
	
	public BaseComponent<?> getComponent() {
		return this.component;
	}
	
	Handle setComponent(BaseComponent<?> component) {
		if (this.component == null) {
			this.component = component;
		}
		return this;
	}
}
