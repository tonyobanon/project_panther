package com.re.paas.internal.templating;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import com.google.common.collect.Maps;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.app_provisioning.AppProvisioner;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.templating.AbstractObjectModelSpiDelegate;
import com.re.paas.api.templating.TemplateObjectModel;
import com.re.paas.api.utils.ClassUtils;

public class ObjectModelSpiDelegate extends AbstractObjectModelSpiDelegate {

	private static final String VELOCITY_INSTANCES_RESOURCE_KEY = "VIRK";

	@Override
	public void init() {

		// Create resource map for storing instances of the velocity engine
		set(VELOCITY_INSTANCES_RESOURCE_KEY, Maps.newHashMap());

		forEach(this::addTemplateObjectModel);
	}

	@Override
	protected void add(List<Class<TemplateObjectModel>> classes) {
		classes.forEach(c -> {
			addTemplateObjectModel(c);
		});
	}
	
	@Override
	protected void remove(List<Class<TemplateObjectModel>> classes) {
		String app = ((AppClassLoader) classes.get(0).getClassLoader()).getAppId();
		getVelocityEngines().remove(app);
	}

	private final Map<String, VelocityEngine> getVelocityEngines() {
		@SuppressWarnings("unchecked")
		Map<String, VelocityEngine> o = (Map<String, VelocityEngine>) get(VELOCITY_INSTANCES_RESOURCE_KEY);
		return o;
	}

	private void addTemplateObjectModel(Class<TemplateObjectModel> c) {

		TemplateObjectModel tm = ClassUtils.createInstance(c);

		String appId = AppProvisioner.get().getAppId(tm.getClass());

		VelocityEngine ve = getVelocityEngines().get(appId);

		if (ve == null) {

			/*
			 * create a new instance of the velocity engine
			 */

			ve = new VelocityEngine();
			ve.setProperty(Velocity.RUNTIME_LOG_NAME, TemplatingModel.class.getSimpleName());
			ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");

			Class<?> loaderClass = null;
			try {

				loaderClass = c.getClassLoader()
						.loadClass("com.ce.saas.internal.templating.api.ClasspathResourceLoader");

			} catch (ClassNotFoundException ex) {
				Exceptions.throwRuntime(ex);
			}

			ve.setProperty("classpath.resource.loader.instance", ClassUtils.createInstance(loaderClass));
			ve.init();

			getVelocityEngines().put(appId, ve);
		}

		Template template = ve.getTemplate(tm.templateName());

		try {

			Method m = c.getMethod("setTemplate", Template.class);
			boolean changedAccessibility = false;

			if (!m.isAccessible()) {
				changedAccessibility = true;
				m.setAccessible(true);
			}

			m.invoke(tm, template);

			if (changedAccessibility) {
				m.setAccessible(false);
			}

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			Exceptions.throwRuntime(e);
		}
	}
	
}
