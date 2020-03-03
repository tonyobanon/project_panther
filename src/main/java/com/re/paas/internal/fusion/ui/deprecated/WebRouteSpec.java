package com.re.paas.internal.fusion.ui.deprecated;

import java.util.List;

import com.re.paas.api.fusion.functionalities.Functionality;

public class WebRouteSpec {

	private String uri;
	private List<Functionality> min;
	private List<Functionality> max;
	
	public List<Functionality> getMin() {
		return min;
	}

	public WebRouteSpec setMin(List<Functionality> min) {
		this.min = min;
		return this;
	}

	public List<Functionality> getMax() {
		return max;
	}

	public WebRouteSpec setMax(List<Functionality> max) {
		this.max = max;
		return this;
	}

	public String getUri() {
		return uri;
	}

	public WebRouteSpec setUri(String uri) {
		this.uri = uri;
		return this;
	}

}
