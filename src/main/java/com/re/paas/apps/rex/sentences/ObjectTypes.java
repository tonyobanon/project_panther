package com.re.paas.apps.rex.sentences;

import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.sentences.Article;
import com.re.paas.api.sentences.ObjectType;
import com.re.paas.apps.rex.functionality.AgentFunctionalities;
import com.re.paas.apps.rex.functionality.AgentOrganizationFunctionalities;

public enum ObjectTypes implements ObjectType {
	
	AGENT_APPLICATION("application", Article.AN, AgentFunctionalities.VIEW_AGENT_APPLICATIONS),
	ORGANIZATION_ADMIN_APPLICATION("application", Article.AN, AgentOrganizationFunctionalities.VIEW_ORGANIZATION_ADMIN_APPLICATIONS);
	
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
}
