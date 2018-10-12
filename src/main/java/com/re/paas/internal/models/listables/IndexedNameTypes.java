package com.re.paas.internal.models.listables;

import com.re.paas.api.listable.IndexedNameType;

public enum IndexedNameTypes implements IndexedNameType {

	USER(1), ACTIVITY_STREAM(2), ADMIN_APPLICATION(3), CRON_JOB(4);

	private final int id;

	private IndexedNameTypes(Integer id) {
		this.id = id;
	}

	public static IndexedNameTypes from(Integer value) {
		switch (value) {
		case 1:
			return USER;
		case 2:
			return ACTIVITY_STREAM;
		case 3:
			return ADMIN_APPLICATION;
		case 4:
			return CRON_JOB;
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	@Override
	public String namespace() {
		return "default";
	}

	@Override
	public Integer id() {
		return id;
	}

}
