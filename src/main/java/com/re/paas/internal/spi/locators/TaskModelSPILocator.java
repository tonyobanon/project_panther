package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;
import com.re.paas.api.tasks.AbstractTaskModelDelegate;
import com.re.paas.api.tasks.TaskImage;

public class TaskModelSPILocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.TASK_MODEL;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("TaskModel");
	}
	
	@Override
	public Class<?> classType() {
		return TaskImage.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractTaskModelDelegate.class;
	}
}
