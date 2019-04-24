package com.re.paas.api.templating;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.utils.ClassUtils;

public abstract class TemplateObjectModelFactory<M extends TemplateObjectModel> extends AbstractResource {

	private final Class<M> templateClass;

	public TemplateObjectModelFactory() {

		super(SpiType.TEMPLATE_OBJECT_MODEL_FACTORY);
		
		@SuppressWarnings("unchecked")
		Class<M> templateClass = (Class<M>) ClassUtils
				.getGenericRefs(getClass().getClassLoader(), getClass().getGenericSuperclass()).get(0);

		if (ClassUtils.equals(templateClass, TemplateObjectModel.class)) {
			// Only subclasses of TemplateObjectModel are allowed
			Exceptions.throwRuntime(
					"Factories may only be created for subclasses of " + TemplateObjectModel.class.getName());
		}

		this.templateClass = templateClass;
	}

	abstract public M create(M template);

	public Class<M> getObjectModelClass() {
		return templateClass;
	}

	public static AbstractObjectModelFactorySpiDelegate getDelegate() {
		return Singleton.get(AbstractObjectModelFactorySpiDelegate.class);
	}

}
