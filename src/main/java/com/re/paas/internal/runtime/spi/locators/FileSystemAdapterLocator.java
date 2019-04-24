package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.infra.filesystem.AbstractFileSystemAdapterDelegate;
import com.re.paas.api.infra.filesystem.FileSystemAdapter;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class FileSystemAdapterLocator extends BaseSPILocator {

	public FileSystemAdapterLocator() {
		addTypeSuffix("Adapter");
	}
	
	@Override
	public SpiType spiType() {
		return SpiType.FILESYSTEM_ADAPTER;
	}

	@Override
	public Class<? extends Resource> classType() {
		return FileSystemAdapter.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractFileSystemAdapterDelegate.class;
	}
	
}
