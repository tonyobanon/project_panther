package com.re.paas.api.fusion;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class BaseComponent {

	private final String id;
	
	private static final Map<String, BaseComponent> components = new HashMap<>();

	protected BaseComponent(String id) {
		this.id = id;
		components.put(this.id, this);
	}

	public String getId() {
		return id;
	}

	public abstract String getAssetId();
	

	protected void invokeBehaviour(String name) {
	}

	protected void addEventListener(String name, Consumer<Map<String, Object>> consumer) {
	}
}
