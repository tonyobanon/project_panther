package com.re.paas.api.infra.database.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Projection {

	private String projectionType;

	private List<String> nonKeyAttributes;

	public void setProjectionType(String projectionType) {
		this.projectionType = projectionType;
	}

	public String getProjectionType() {
		return this.projectionType;
	}

	public Projection withProjectionType(String projectionType) {
		setProjectionType(projectionType);
		return this;
	}

	public void setProjectionType(ProjectionType projectionType) {
		withProjectionType(projectionType);
	}

	public Projection withProjectionType(ProjectionType projectionType) {
		this.projectionType = projectionType.toString();
		return this;
	}

	public java.util.List<String> getNonKeyAttributes() {
		return nonKeyAttributes;
	}

	public void setNonKeyAttributes(Collection<String> nonKeyAttributes) {
		if (nonKeyAttributes == null) {
			this.nonKeyAttributes = null;
			return;
		}

		this.nonKeyAttributes = new ArrayList<String>(nonKeyAttributes);
	}

	public Projection withNonKeyAttributes(String... nonKeyAttributes) {
		if (this.nonKeyAttributes == null) {
			setNonKeyAttributes(new ArrayList<String>(nonKeyAttributes.length));
		}
		for (String ele : nonKeyAttributes) {
			this.nonKeyAttributes.add(ele);
		}
		return this;
	}

	public Projection withNonKeyAttributes(Collection<String> nonKeyAttributes) {
		setNonKeyAttributes(nonKeyAttributes);
		return this;
	}

}