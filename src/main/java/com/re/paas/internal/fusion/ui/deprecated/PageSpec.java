package com.re.paas.internal.fusion.ui.deprecated;

import java.util.List;

public class PageSpec {

	private boolean isParent;
	private List<PageParentSpec> parents;

	
	public boolean isParent() {
		return isParent;
	}

	public PageSpec setParent(boolean isParent) {
		this.isParent = isParent;
		return this;
	}

	public List<PageParentSpec> getParents() {
		return parents;
	}
	
	public PageSpec setParents(List<PageParentSpec> parents) {
		this.parents = parents;
		return this;
	}	
	
}
