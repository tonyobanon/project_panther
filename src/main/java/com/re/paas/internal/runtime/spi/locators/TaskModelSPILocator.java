package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.tasks.AbstractTaskModelDelegate;
import com.re.paas.api.tasks.TaskImage;

public class TaskModelSPILocator extends BaseSPILocator {
	
	@Override
	public SpiType spiType() {
		return SpiType.TASK_MODEL;
	}
	
	@Override
	public Class<? extends AbstractResource> classType() {
		return TaskImage.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractTaskModelDelegate.class;
	}
}
