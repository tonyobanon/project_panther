package com.re.paas.internal.templating;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.ResourceStatus;
import com.re.paas.api.templating.AbstractObjectModelFactorySpiDelegate;
import com.re.paas.api.templating.TemplateObjectModel;
import com.re.paas.api.templating.TemplateObjectModelFactory;

public class ObjectModelFactorySpiDelegate extends AbstractObjectModelFactorySpiDelegate {

	private static final String TMF_NAMESPACE = "tmf";

	@Override
	public DelegateInitResult init() {
		
		createResourceMaps();
		
		return forEach(this::add0);
	}

	@Override
	protected Collection<?> getResourceObjects() {
		return getTemplateObjectModelFactories().values();
	}
	
	@Override
	protected ResourceStatus add(Class<TemplateObjectModelFactory<? extends TemplateObjectModel>> clazz) {
		return add0(clazz);
	}

	@Override
	protected ResourceStatus remove(Class<TemplateObjectModelFactory<? extends TemplateObjectModel>> clazz) {
		return remove0(clazz);
	}

	@Override
	public Map<Class<?>, TemplateObjectModelFactory<? extends TemplateObjectModel>> resources() {

		Map<Class<?>, TemplateObjectModelFactory<? extends TemplateObjectModel>> map = Maps.newHashMap();

		getTemplateObjectModelFactories().forEach((k, v) -> {
			map.put((Class<?>) k, (TemplateObjectModelFactory<?>) v);
		});

		return map;
	}

	private ResourceStatus remove0(Class<TemplateObjectModelFactory<? extends TemplateObjectModel>> clazz) {

		Map<Class<?>, TemplateObjectModelFactory<? extends TemplateObjectModel>> resources = resources();

		Set<Entry<Class<?>, TemplateObjectModelFactory<? extends TemplateObjectModel>>> set = resources.entrySet();

		for (Entry<Class<?>, TemplateObjectModelFactory<? extends TemplateObjectModel>> e : set) {
			if (e.getValue().getClass().equals(clazz)) {
				set.remove(e);
			}
		}
		
		return ResourceStatus.UPDATED;
	}

	private ResourceStatus add0(Class<TemplateObjectModelFactory<? extends TemplateObjectModel>> c) {
		TemplateObjectModelFactory<? extends TemplateObjectModel> tmf = com.re.paas.internal.classes.ClassUtil.createInstance(c);
		getTemplateObjectModelFactories().put(tmf.getObjectModelClass(), tmf);
		return ResourceStatus.UPDATED;
	}

	private void createResourceMaps() {
		getLocalStore().put(TMF_NAMESPACE, new HashMap<>());
	}

	@SuppressWarnings("unchecked")
	private Map<Class<? extends TemplateObjectModel>, TemplateObjectModelFactory<? extends TemplateObjectModel>> getTemplateObjectModelFactories() {
		return (Map<Class<? extends TemplateObjectModel>, TemplateObjectModelFactory<? extends TemplateObjectModel>>) getLocalStore().get(TMF_NAMESPACE);
	}

}
