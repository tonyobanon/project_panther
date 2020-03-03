package com.re.paas.api.sentences;

import com.re.paas.api.fusion.functionalities.Functionality;

public interface ObjectType {
	
	Article getArticle();

	Functionality getFunctionality();
	
	String title();
}
