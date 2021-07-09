package com.re.paas.api.infra.database.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Projection {

	private ProjectionType projectionType;

	private List<String> nonKeyAttributes;

	public Projection(ProjectionType projectionType) {
		this(projectionType, Collections.emptyList());
	}
	
	public Projection(ProjectionType projectionType, List<String> nonKeyAttributes) {
		this.projectionType = projectionType;
		this.nonKeyAttributes = nonKeyAttributes;
	}

	public ProjectionType getProjectionType() {
		return this.projectionType;
	}

	public void setProjectionType(ProjectionType projectionType) {
		withProjectionType(projectionType);
	}

	public Projection withProjectionType(ProjectionType projectionType) {
		this.projectionType = projectionType;
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