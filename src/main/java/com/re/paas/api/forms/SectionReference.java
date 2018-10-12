package com.re.paas.api.forms;

import com.re.paas.api.realms.Realm;

public interface SectionReference extends Reference {

	Realm realm();
	
	boolean importFields();
}
