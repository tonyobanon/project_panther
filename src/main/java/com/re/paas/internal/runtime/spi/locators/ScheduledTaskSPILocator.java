package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.tasks.AbstractTaskDelegate;
import com.re.paas.api.tasks.TaskModel;

public class ScheduledTaskSPILocator extends BaseSPILocator {

	@Override
	public SpiType spiType() {
		return SpiType.SCHEDULED_TASK;
	}

	@Override
	public Class<? extends AbstractResource> classType() {
		return TaskModel.class;
	}

	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractTaskDelegate.class;
	}
}
