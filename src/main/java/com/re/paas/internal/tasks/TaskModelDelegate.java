package com.re.paas.internal.tasks;

import java.util.Map;

import com.google.common.collect.Maps;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.tasks.AbstractTaskModelDelegate;
import com.re.paas.api.tasks.TaskImage;
import com.re.paas.api.utils.ClassUtils;

public class TaskModelDelegate extends AbstractTaskModelDelegate {

	@Override
	public DelegateInitResult init() {
		forEach(c -> {
			TaskImage model = ClassUtils.createInstance(c);
			set(model.name(), model);
		});
		return DelegateInitResult.SUCCESS;
	}
	
	@Override
	public Map<String, ClientRBRef> getTaskModelNames() {
		Map<String, ClientRBRef> result = Maps.newHashMap();
		getAll().values().forEach((v) -> {
			TaskImage m = (TaskImage) v;
			result.put(m.name(), m.title());
		});
		return result;
	}
	
	@Override
	public TaskImage getTaskModel(String name) {
		return (TaskImage) super.get(name);
	}
}
