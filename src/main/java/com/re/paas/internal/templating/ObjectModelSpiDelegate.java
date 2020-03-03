package com.re.paas.internal.templating;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import com.google.common.collect.Maps;
import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.runtime.ClassLoaders;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.ResourceStatus;
import com.re.paas.api.templating.AbstractObjectModelSpiDelegate;
import com.re.paas.api.templating.TemplateObjectModel;
import com.re.paas.internal.classes.ClassUtil;

public class ObjectModelSpiDelegate extends AbstractObjectModelSpiDelegate {

	private static final String VELOCITY_INSTANCES_RESOURCE_KEY = "VIRK";

	@Override
	public DelegateInitResult init() {

		// Create resource map for storing instances of the velocity engine
		getLocalStore().put(VELOCITY_INSTANCES_RESOURCE_KEY, Maps.newHashMap());

		return forEach(this::add0);
	}
	
	@Override
	protected Collection<?> getResourceObjects() {
		return getVelocityEngines().values();
	}

	@Override
	protected ResourceStatus add(Class<TemplateObjectModel> clazz) {
		return add0(clazz);
	}
	
	@Override
	protected ResourceStatus remove(Class<TemplateObjectModel> clazz) {
		String app = ((AppClassLoader) clazz.getClassLoader()).getAppId();
		VelocityEngine e = getVelocityEngines().remove(app);
		return e != null  ? ResourceStatus.UPDATED : ResourceStatus.NOT_UPDATED;
	}

	private final Map<String, VelocityEngine> getVelocityEngines() {
		@SuppressWarnings("unchecked")
		Map<String, VelocityEngine> o = (Map<String, VelocityEngine>) getLocalStore().get(VELOCITY_INSTANCES_RESOURCE_KEY);
		return o;
	}

	private ResourceStatus add0(Class<TemplateObjectModel> c) {

		ResourceStatus rStatus = ResourceStatus.NOT_UPDATED;
		
		TemplateObjectModel tm = ClassUtil.createInstance(c);

		String appId = ClassLoaders.getId(tm.getClass());

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
						.loadClass("com.re.paas.internal.templating.api.ClasspathResourceLoader");

			} catch (ClassNotFoundException ex) {
				Exceptions.throwRuntime(ex);
			}

			ve.setProperty("classpath.resource.loader.instance", ClassUtil.createInstance(loaderClass));
			ve.init();

			getVelocityEngines().put(appId, ve);
			
			rStatus = ResourceStatus.UPDATED;
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
		
		return rStatus;
	}
	
}
