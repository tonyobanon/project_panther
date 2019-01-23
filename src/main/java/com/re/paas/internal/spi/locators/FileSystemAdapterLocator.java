package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.infra.filesystem.AbstractFileSystemAdapterDelegate;
import com.re.paas.api.infra.filesystem.FileSystemAdapter;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;

public class FileSystemAdapterLocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.FILESYSTEM_ADAPTER;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("Adapter");
	}

	@Override
	public Class<?> classType() {
		return FileSystemAdapter.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractFileSystemAdapterDelegate.class;
	}
	
}
