package com.re.paas.internal.runtime.spi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.fusion.JsonArray;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.ClassLoaders;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.api.utils.Utils;

/**
 * Because of the arbitrary complexity that may arise while maintaining SPI
 * delegate configuration, this class was created to encapsulate the logic used
 * to load delegate classes
 * 
 * @author anthonyanyanwu
 *
 */
public class SpiDelegateConfigHandler {

	private static final Logger LOG = Logger.get(SpiDelegateConfigHandler.class);

	private static final Path defaultPath = SpiBaseImpl.spiConfigBasePath.resolve("default_delegates.json");
	private static final Path platformPath = SpiBaseImpl.spiConfigBasePath.resolve("platform_delegates.json");
	private static final Path availablePath = SpiBaseImpl.spiConfigBasePath.resolve("available_delegates.json");

	static JsonObject platformConfig;
	static JsonObject defaultConfig;
	static JsonObject availableConfig;

	static void init() {

		// Prepare config files
		createResourceFiles();

		// Parse file and save to memory
		platformConfig = Utils.getJson(platformPath);
		defaultConfig = Utils.getJson(defaultPath);
		availableConfig = Utils.getJson(availablePath);
	}

	private static void createResourceFiles() {
		try {

			/* ===== Platform Delegates ===== */

			LOG.debug("Registering platform delegates");

			if (Files.exists(platformPath)) {
				Files.delete(platformPath);
			}

			JsonObject platformDelegatesConfig = new JsonObject();
			for (SpiType type : SpiType.values()) {
				String key = getDelegateConfigKey(type);
				
				Class<?> clazz = com.re.paas.internal.classes.ClassUtil.forName(ClassLoaders.getConfiguration().getString(key));
				platformDelegatesConfig.put(type.toString(), ClassUtils.asString(clazz));
			}

			Files.createFile(platformPath);
			Utils.saveString(platformDelegatesConfig.toString(), platformPath);

			/* ===== Default Delegates ===== */

			if (!Files.exists(defaultPath)) {

				LOG.debug("Creating default delegates configuration");

				Files.createFile(defaultPath);
				Utils.saveString("{}", defaultPath);
			}

			/* ===== Available Delegates ===== */

			if (!Files.exists(availablePath)) {

				LOG.debug("Creating available delegates configuration");

				Files.createFile(availablePath);

				JsonObject contents = new JsonObject();
				for (SpiType type : SpiType.values()) {
					contents.put(type.toString(), new JsonArray());
				}
				Utils.saveString(contents.toString(), availablePath);
			}

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	static Class<? extends SpiDelegate<?>> get(SpiType type, Tier... tiers) {

		for (Tier tier : tiers) {

			JsonObject config = tier.getConfig();

			String key = type.toString();
			Object value = config.containsKey(key) ? config.getValue(key) : null;

			if (value instanceof JsonArray) {
				value = ((JsonArray) value).getValue(0);
			}

			if (value != null) {
				return com.re.paas.internal.classes.ClassUtil.forName(value.toString());
			}
		}

		return null;
	}

	static synchronized void put(SpiType type, Class<? extends SpiDelegate<?>> delegateClass, Tier... tiers) {

		if (type.classification().requiresTrustedDelegate()
				&& !SpiBaseImpl.isAppTrusted(ClassLoaders.getId(delegateClass))) {

			Exceptions.throwRuntime(new SecurityException("Type: " + type.toString() + " requires a trusted delegate"));
		}

		String className = ClassUtils.asString(delegateClass);

		for (Tier tier : tiers) {

			JsonObject config = tier.getConfig();

			String key = type.toString();

			Object value = config.containsKey(key) ? config.getValue(key) : null;

			if (value != null && value instanceof JsonArray) {

				JsonArray arrayValue = (JsonArray) value;

				if (!arrayValue.contains(className)) {
					
					arrayValue.add(className);
					
				} else {
					continue;
				}

			} else {

				if (value instanceof String) {

					if (value.equals(className)) {
						continue;
					}
				}
				config.put(key, className);
			}

			Utils.saveString(config.toString(), tier.getPath());
		}
	}

	static synchronized void remove(SpiType type, Class<? extends SpiDelegate<?>> delegateClass, Tier... tiers) {

		if (type.classification().requiresTrustedDelegate()
				&& !SpiBaseImpl.isAppTrusted(ClassLoaders.getId(delegateClass))) {
			Exceptions.throwRuntime(new SecurityException("Type: " + type.toString() + " requires a trusted delegate"));
		}

		String className = ClassUtils.asString(delegateClass);

		for (Tier tier : tiers) {

			JsonObject config = tier.getConfig();

			String key = type.toString();

			Object value = config.containsKey(key) ? config.getValue(key) : null;

			if (value != null && value instanceof JsonArray) {

				JsonArray arrayValue = (JsonArray) value;

				if (arrayValue.contains(className)) {
					
					arrayValue.remove(className);
					
				} else {
					continue;
				}

			} else {

				if (value instanceof String) {

					if (value.equals(className)) {
						
						config.remove(key);
						
					} else {
						continue;
					}
				}
			}

			if(value != null) {
				Utils.saveString(config.toString(), tier.getPath());
			}
		}
	}

	private static String getDelegateConfigKey(SpiType type) {
		return "platform.spi." + type.toString().toLowerCase() + ".delegate";
	}

	static enum Tier {

		PLATFORM(platformConfig, platformPath), DEFAULT(defaultConfig, defaultPath),
		AVAILABLE(availableConfig, availablePath);

		private final JsonObject config;
		private final Path path;

		private Tier(JsonObject config, Path path) {
			this.config = config;
			this.path = path;
		}

		public JsonObject getConfig() {
			return config;
		}

		public Path getPath() {
			return path;
		}
		
		public static Tier[] all() {
			return new Tier[] {DEFAULT, PLATFORM, AVAILABLE};
		}
		
		public static Tier[] onlyDefaultAndAvailable() {
			return new Tier[] {DEFAULT, AVAILABLE};
		}
		
		public static Tier[] onlyAvailable() {
			return new Tier[] {AVAILABLE};
		}
		
		public static Tier[] onlyDefault() {
			return new Tier[] {DEFAULT};
		}
	}

}
