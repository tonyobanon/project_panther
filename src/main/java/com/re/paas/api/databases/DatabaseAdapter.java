package com.re.paas.api.databases;

import java.util.List;

import com.re.paas.api.forms.Section;

public interface DatabaseAdapter {
	
	String name();
	
	String title();
	
	String icon();

	List<Section> initForm();

	String schemaFactory();
}
