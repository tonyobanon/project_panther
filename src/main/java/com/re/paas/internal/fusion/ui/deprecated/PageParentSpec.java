package com.re.paas.internal.fusion.ui.deprecated;

import java.util.List;

public class PageParentSpec {

	private String name;
	private List<String> params;
	private String path;
	private List<IntersectionNode> intersectionNodes;
	private Boolean setOriginUri = true;
	private String originUri;
	
	
	public String getName() {
		return name;
	}

	public PageParentSpec setName(String name) {
		this.name = name;
		return this;
	}
	
	public List<String> getParams() {
		return params;
	}

	public PageParentSpec setParams(List<String> params) {
		this.params = params;
		return this;
	}

	public String getPath() {
		return path;
	}

	public PageParentSpec setPath(String path) {
		this.path = path;
		return this;
	}

	public List<IntersectionNode> getIntersectionNodes() {
		return intersectionNodes;
	}

	public PageParentSpec setIntersectionNodes(List<IntersectionNode> intersectionNodes) {
		this.intersectionNodes = intersectionNodes;
		return this;
	}

	public Boolean getSetOriginUri() {
		return setOriginUri;
	}

	public PageParentSpec setSetOriginUri(Boolean setOriginUri) {
		this.setOriginUri = setOriginUri;
		return this;
	}

	public String getOriginUri() {
		return originUri;
	}

	public PageParentSpec setOriginUri(String originUri) {
		this.originUri = originUri;
		return this;
	}
}
