package com.re.paas.api.infra.database.model;

public class GlobalSecondaryIndexUpdate {

	private Action action;

	private IndexDefinition definition;

	public Action getAction() {
		return action;
	}

	public GlobalSecondaryIndexUpdate setAction(Action action) {
		this.action = action;
		return this;
	}

	public IndexDefinition getDefinition() {
		return definition;
	}

	public GlobalSecondaryIndexUpdate setDefinition(IndexDefinition definition) {
		this.definition = definition;
		return this;
	}
	
	public static enum Action {
		 CREATE, DELETE
	}
	
}
