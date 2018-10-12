package com.re.paas.internal.classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Maps;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.logging.Logger;
import com.re.paas.internal.errors.ParameterError;

public abstract class Configuration {

	private Map<String, String> properties = Maps.newConcurrentMap();
	
	protected abstract String filePath();
	
	private void load() {
		
		String filePath = filePath();

		Logger.get().info("Reading " + filePath);
		
		InputStream in = null;
		try {
			in = new FileInputStream(new File(filePath));
		} catch (FileNotFoundException e) {
			Exceptions.throwRuntime(e);
		}

		Properties o = new Properties();
		try {
			o.load(in);
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
		
		o.forEach((k, v) -> {
			properties.put((String) k, ((String) v).trim());
		});
	}
	
	public String get(String property) {
		
		if(properties.isEmpty()) {
			load();
		}
		
		String v = properties.get(property);
		if ((v != null) && (v != "")) {
			return v;
		} else {
			return (String) Exceptions.throwRuntime(PlatformException.get(ParameterError.EMPTY_PARAMETER,
					 property));
		}
	}

	public Integer getInteger(String property) {
		Integer val = null;
		try {
			val = Integer.parseInt(get(property));
		} catch (NumberFormatException e) {
			return (Integer) Exceptions.throwRuntime(PlatformException.get(ParameterError.INVALID_PARAMETER,
					property));
		}
		return val;
	}

	public String getOrNull(String property) {
		String v = properties.get(property);
		if ((v != null) && (!v.equals(""))) {
			return v;
		} else {
			return null;
		}
	}

}
