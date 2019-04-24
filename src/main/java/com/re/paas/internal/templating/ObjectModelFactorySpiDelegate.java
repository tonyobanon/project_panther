package com.re.paas.internal.templating;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.templating.AbstractObjectModelFactorySpiDelegate;
import com.re.paas.api.templating.TemplateObjectModel;
import com.re.paas.api.templating.TemplateObjectModelFactory;
import com.re.paas.api.utils.ClassUtils;

public class ObjectModelFactorySpiDelegate extends AbstractObjectModelFactorySpiDelegate {

	private static final String TMF_NAMESPACE = "tmf";

	@Override
	public DelegateInitResult init() {
		createResourceMaps();
		forEach(this::addTemplateObjectModelFactory);
		return DelegateInitResult.SUCCESS;
	}

	@Override
	protected void add(List<Class<TemplateObjectModelFactory<? extends TemplateObjectModel>>> classes) {
		classes.forEach(c -> {
			addTemplateObjectModelFactory(c);
		});
	}

	@Override
	protected List<Class<TemplateObjectModelFactory<? extends TemplateObjectModel>>> remove(
			List<Class<TemplateObjectModelFactory<? extends TemplateObjectModel>>> classes) {
		classes.forEach(c -> {
			removeTemplateObjectModelFactory(c);
		});
		return Collections.emptyList();
	}

	@Override
	public Map<Class<?>, TemplateObjectModelFactory<? extends TemplateObjectModel>> resources() {

		Map<Class<?>, TemplateObjectModelFactory<? extends TemplateObjectModel>> map = Maps.newHashMap();

		getTemplateObjectModelFactories().forEach((k, v) -> {
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
		getTemplateObjectModelFactories().put(tmf.getObjectModelClass(), tmf);
	}

	private void createResourceMaps() {
		set(TMF_NAMESPACE, new HashMap<>());
	}

	@SuppressWarnings("unchecked")
	private Map<Class<? extends TemplateObjectModel>, TemplateObjectModelFactory<? extends TemplateObjectModel>> getTemplateObjectModelFactories() {
		return (Map<Class<? extends TemplateObjectModel>, TemplateObjectModelFactory<? extends TemplateObjectModel>>) get(
				TMF_NAMESPACE);
	}

}
