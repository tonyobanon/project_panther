package com.re.paas.internal.filesystems;

import com.re.paas.api.events.BaseEvent;

public class FileSystemChangeEvent extends BaseEvent {

	private static final long serialVersionUID = 1L;

	@Override
	public String name() {
		return "FileSystemChangeEvent";
	}

}
