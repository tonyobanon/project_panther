package com.re.paas.api.adapters;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.infra.filesystem.NativeFileSystem;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.utils.JsonParser;
import com.re.paas.api.utils.Utils;

public class AdapterConfig {
	
	private static final Logger LOG = LoggerFactory.get().getLog(AdapterConfig.class);

	private static transient final Map<AdapterType, AdapterConfig> instances = Collections
			.synchronizedMap(new HashMap<AdapterType, AdapterConfig>());

	private transient final AdapterType type;
	private transient Path basePath = NativeFileSystem.get().getResourcePath().resolve("adapter_config");
	
	private transient final Path file;

	private String adapterName;
	private Map<String, String> fields;

	public AdapterConfig(AdapterType type) {
		this.type = type;
		this.file = basePath.resolve(type.name().toLowerCase() + ".json");
	}

	public AdapterConfig load() {

		if (!Files.exists(file)) {
			LOG.warn("Adapter config file: " + file.toString() + " does not exist");
			return null;
		}

		AdapterConfig instance = instances.get(type);

		if (instance == null) {
			instance = JsonParser.get().fromString(Utils.getString(file), AdapterConfig.class);
			instances.put(type, instance);
		}

		this.adapterName = instance.adapterName;
		this.fields = instance.fields;

		return this;
	}

	public AdapterConfig save() {

		try {

			if (!Files.exists(file)) {

				Files.createDirectories(file);
				Files.createFile(file);
			}

			Utils.saveString(toString(), file);

		} catch (Exception e) {
			Exceptions.throwRuntime(e);
		}

		instances.put(type, this);

		return this;
	}
	
	public String toString() {
		return JsonParser.get().toString(this);
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
