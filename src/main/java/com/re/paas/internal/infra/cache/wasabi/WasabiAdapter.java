package com.re.paas.internal.infra.cache.wasabi;

import java.util.HashMap;
import java.util.Map;

import com.re.paas.internal.infra.cache.s3.S3Adapter;

public class WasabiAdapter extends S3Adapter {
	
	@Override
	public String name() {
		return "wasabi";
	}

	@Override
	public String title() {
		return "wasabi";
	}
	
	@Override
	public String iconUrl() {
		return "https://www.komprise.com/wp-content/uploads/2018/03/Wasabi-web-300x200.png";
	}

	protected String formTitle() {
		return "Wasabi Credentials";
	}
	
	@Override
	protected String endpoint() {
		return "s3.wasabisys.com";
	}
	
	@Override
	protected Map<String, String> availableRegions() {
		
		String[] regions = {"us-east-1", "us-west-1", "eu-central-1"};
		
		Map<String, String> regionsMap = new HashMap<>(regions.length);
		
		for(int i = 0; i < regions.length; i++) {
			regionsMap.put(regions[i], regions[i]);
		}
		
		return regionsMap;
	}
	
}