package com.re.paas.api.adapters;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.Platform;
import com.re.paas.internal.classes.Json;

public class AdapterConfig {

	private static final Map<AdapterType, AdapterConfig> instances = Collections
			.synchronizedMap(new HashMap<AdapterType, AdapterConfig>());

	private final AdapterType type;
	private final Path file;

	private String adapterName;
	private Map<String, String> fields;

	public AdapterConfig(AdapterType type) {
		this.type = type;
		this.file = Platform.getResourcePath().resolve("config").resolve(type.name().toLowerCase() + ".json");
	}

	public AdapterConfig load() {

		if (!Files.exists(file)) {
			Exceptions.throwRuntime(new FileNotFoundException("Adapter config file: " + file.toString() + " does not exist"));
		}

		AdapterConfig instance = instances.get(type);

		if (instance == null) {
			instance = Json.fromJson(Utils.getString(file), AdapterConfig.class);
			instances.put(type, instance);
		}

		this.adapterName = instance.adapterName;
		this.fields = instance.fields;

		return this;
	}

	public AdapterConfig save() {

		Utils.saveString(Json.getGson().toJson(this), file);
		instances.put(type, this);

		return this;
	}
	
	public AdapterType getType() {
		return type;
	}

	protected Path getFile() {
		return file;
	}

	public String getAdapterName() {
		return adapterName;
	}

	public AdapterConfig setAdapterName(String adapterName) {
		this.adapterName = adapterName;
		return this;
	}

	public Map<String, String> getFields() {
		return fields;
	}

	public AdapterConfig setFields(Map<String, String> fields) {
		this.fields = fields;
		return this;
	}
	
}
