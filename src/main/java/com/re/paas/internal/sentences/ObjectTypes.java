package com.re.paas.internal.sentences;

import com.re.paas.api.fusion.functionalities.Functionality;
import com.re.paas.api.sentences.Article;
import com.re.paas.api.sentences.ObjectType;
import com.re.paas.integrated.fusion.functionalities.PlatformFunctionalities;
import com.re.paas.integrated.fusion.functionalities.RoleFunctionalities;
import com.re.paas.integrated.fusion.functionalities.UserApplicationFunctionalities;

public enum ObjectTypes implements ObjectType {

	ADMIN_APPLICATION("application", Article.AN, UserApplicationFunctionalities.VIEW_ADMIN_APPLICATIONS),
	
	SYSTEM_CACHE("system_cache", Article.THE, PlatformFunctionalities.MANAGE_SYSTEM_CACHES),

	SYSTEM_CONFIGURATION("system_configuration", Article.THE, PlatformFunctionalities.VIEW_SYSTEM_CONFIGURATION),

	APPLICATION_FORM("application_form", Article.AN, UserApplicationFunctionalities.MANAGE_APPLICATION_FORMS),

	SYSTEM_CONFIGURATION_FORM("system_configuration_form", Article.THE, PlatformFunctionalities.MANAGE_SYSTEM_CONFIGURATION_FORM),

	SYSTEM_ROLE("system_role", Article.A, RoleFunctionalities.MANAGE_ROLES),
	
	
	//For these kinds, Articles must be defined manually by callers
	
	EMAIL("email"),
	
	PHONE_NUMBER("phone_number"),
	
	PASSWORD("password"),
	
	IMAGE("image"),
		
	USER_ROLE("user_role", Article.THE);
	
	private final String title;
	private final Article article;
	private final Functionality functionality;

	private ObjectTypes(String title) {
		this(title, null);
	}
	
	private ObjectTypes(String title, Article article) {
		this(title, article, null);
	}
	
	private ObjectTypes(String title, Article article, Functionality functionality) {
		this.title = title;
		this.article = article;
		this.functionality = functionality;
	}
	
	public Article getArticle() {
		return article;
	}
	
	public Functionality getFunctionality() {
		return functionality;
	}
	
	@Override
	public String title() {
		return title;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
}
