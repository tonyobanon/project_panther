package com.re.paas.api.tasks;

import java.util.Map;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.runtime.spi.SpiDelegate;

public abstract class AbstractTaskModelDelegate extends SpiDelegate<TaskImage> {

	public abstract Map<String, ClientRBRef> getTaskModelNames();
	
	public abstract TaskImage getTaskModel(String name);
	
}
