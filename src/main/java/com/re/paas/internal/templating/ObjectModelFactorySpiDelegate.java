package com.re.paas.internal.templating;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;
import com.re.paas.api.spi.SpiTypes;
import com.re.paas.api.templating.AbstractObjectModelFactorySpiDelegate;
import com.re.paas.api.templating.TemplateObjectModel;
import com.re.paas.api.templating.TemplateObjectModelFactory;
import com.re.paas.api.utils.ClassUtils;

public class ObjectModelFactorySpiDelegate extends AbstractObjectModelFactorySpiDelegate {

	@Override
	public void init() {
		forEach(this::addTemplateObjectModelFactory);
	}

	@Override
	protected void add(List<Class<TemplateObjectModelFactory<? extends TemplateObjectModel>>> classes) {
		classes.forEach(c -> {
			addTemplateObjectModelFactory(c);
		});
	}

	@Override
	protected void remove(List<Class<TemplateObjectModelFactory<? extends TemplateObjectModel>>> classes) {
		classes.forEach(c -> {
			removeTemplateObjectModelFactory(c);
		});
	}
	
	@Override
	public Map<Class<?>, TemplateObjectModelFactory<? extends TemplateObjectModel>> resources() {

		Map<Class<?>, TemplateObjectModelFactory<? extends TemplateObjectModel>> map = Maps.newHashMap();

		getAll(SpiTypes.TEMPLATE_OBJECT_MODEL_FACTORY).forEach((k, v) -> {
			map.put((Class<?>) k, (TemplateObjectModelFactory<?>) v);
		});

		return map;
	}

	private void removeTemplateObjectModelFactory(Class<TemplateObjectModelFactory<? extends TemplateObjectModel>> c) {
		
		Map<Class<?>, TemplateObjectModelFactory<? extends TemplateObjectModel>> resources = resources();
		
		Set<Entry<Class<?>, TemplateObjectModelFactory<? extends TemplateObjectModel>>> set = resources.entrySet();
		
		for (Entry<Class<?>, TemplateObjectModelFactory<? extends TemplateObjectModel>> e : set) {
			if (e.getValue().getClass().equals(c)) {
				set.remove(e);
			}
		}
	}

	private void addTemplateObjectModelFactory(Class<TemplateObjectModelFactory<? extends TemplateObjectModel>> c) {
		TemplateObjectModelFactory<? extends TemplateObjectModel> tmf = ClassUtils.createInstance(c);
		set(tmf.getObjectModelClass(), tmf);
	}

}
