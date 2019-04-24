package com.re.paas.api;

import java.util.Map;

import com.re.paas.api.adapters.AdapterType;
import com.re.paas.api.forms.Form;
import com.re.paas.api.runtime.spi.Resource;

public interface Adapter<T> extends Resource {

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
	
	T getResource(Map<String, String> fields);
}
