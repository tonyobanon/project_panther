package com.re.paas.api.sentences;

import com.re.paas.api.fusion.services.Functionality;

public interface ObjectType {
	
	Article getArticle();

	Functionality getFunctionality();
	
	String title();
}
