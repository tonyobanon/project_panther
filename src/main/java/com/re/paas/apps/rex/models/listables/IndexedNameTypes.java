package com.re.paas.apps.rex.models.listables;

import com.re.paas.api.listable.IndexedNameType;

public enum IndexedNameTypes implements IndexedNameType {

	PROPERTY_DESCRIPTOR(1), ORGANIZATION_ADMIN_APPLICATION(2), AGENT_APPLICATION(3), AGENT_ORGANIZATION(4), AGENT(5),
	AGENT_ORGANIZATION_MESSAGE(6), AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGE(7), AGENT_ORGANIZATION_REVIEW(8),
	PROPERTY_SPEC(9);

	private final int id;

	private IndexedNameTypes(Integer id) {
		this.id = id;
	}

	public static IndexedNameTypes from(Integer value) {
		switch (value) {
		case 1:
			return PROPERTY_DESCRIPTOR;
		case 2:
			return ORGANIZATION_ADMIN_APPLICATION;
		case 3:
			return AGENT_APPLICATION;
		case 4:
			return AGENT_ORGANIZATION;
		case 5:
			return AGENT;
		case 6:
			return AGENT_ORGANIZATION_MESSAGE;
		case 7:
			return AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGE;
		case 8:
			return AGENT_ORGANIZATION_REVIEW;
		case 9:
			return PROPERTY_SPEC;
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	@Override
	public Integer id() {
		return id;
	}

	@Override
	public String namespace() {
		return "rex";
	}
}
