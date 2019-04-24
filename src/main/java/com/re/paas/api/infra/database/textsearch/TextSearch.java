package com.re.paas.api.infra.database.textsearch;

import com.re.paas.api.classes.IndexedNameSpec;

public interface TextSearch {
	
	public void add(IndexedNameSpec spec, Integer type);
	
	void remove(IndexedNameSpec spec, Integer type);
	
	void get(Integer type, String phrase);
}
