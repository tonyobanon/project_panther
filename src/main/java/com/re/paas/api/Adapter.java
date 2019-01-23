package com.re.paas.api;

import com.re.paas.api.adapters.AdapterType;
import com.re.paas.api.forms.Form;

public interface Adapter {
	
	String name();
	
	String title();
	
	default String description() {
		return null;
	}
	
	String iconUrl();

	Form initForm();
	
	default String initFormClientHelper() {
		return null;
	}
	
	AdapterType getType();
}
