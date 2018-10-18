package com.re.paas.internal.classes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.JsonObject;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.Application;

/**
 * This class provides static helper methods to read config files, as well as scan the
 * classpath.
 * 
 * @author Tony
 */
public class AppDirectory {

	private static ClassLoader classloader;
	private static JsonObject config;

	public static Path getBasePath() {
		try {
			return Paths.get(classloader.getResource("").toURI());
		} catch (URISyntaxException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	public static ClassLoader getBaseClassloader() {
		return classloader;
	}

	public static Path getPath(String resource) {
		try {
			return Paths.get(classloader.getResource(resource).toURI());
		} catch (URISyntaxException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	public static InputStream getInputStream(String resource) {
		return classloader.getResourceAsStream(resource);
	}

	public static OutputStream getOutputStream(String resource) {
		try {
			return Files.newOutputStream(getPath(resource));
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	public static JsonObject getConfig() {
		return config;
	}

	static {
		
		classloader = Application.class.getClassLoader();
		
		try {
			config = Utils.getJson(AppDirectory.getInputStream("config.json"));
		} catch (Exception e) {
			throw new RuntimeException("Error occured while loading config.json", e);
		}
	}

}
