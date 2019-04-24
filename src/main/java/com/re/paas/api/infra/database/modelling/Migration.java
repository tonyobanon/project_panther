package com.re.paas.api.infra.database.modelling;

import com.re.paas.api.infra.database.document.Database;

public interface Migration {
	
	default String description() {
		return null;
	}
	
	String version();
	
	void run(Database database);
}
