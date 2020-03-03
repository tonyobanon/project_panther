package com.re.paas.internal.infra.filesystem;

import com.re.paas.api.events.BaseEvent;

public class FileSystemChangeEvent extends BaseEvent {

	private static final long serialVersionUID = 1L;
	private final FileSystemAdapterConfig config;

	public FileSystemChangeEvent(FileSystemAdapterConfig config) {
		this.config = config;
	}

	public FileSystemAdapterConfig getConfig() {
		return config;
	}

}
