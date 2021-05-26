package com.re.paas.api.fusion;

import java.util.Map;
import java.util.function.Consumer;

public abstract class BaseComponent {

	private final String id;

	protected BaseComponent(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public abstract String getAssetId();

	public String getName() {
		return getAssetId();
	}

	protected void invokeBehaviour(String name) {
	}

	protected void addEventListener(String name, Consumer<Map<String, Object>> consumer) {
	}
}
