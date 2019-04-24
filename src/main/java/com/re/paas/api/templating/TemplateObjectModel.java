package com.re.paas.api.templating;

import java.io.StringWriter;
import java.lang.reflect.Field;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.SpiType;

/**
 * In practice, subclasses of this class, are typically used by model methods
 * for to orchestrate some of their functionalities.
 */
public abstract class TemplateObjectModel extends AbstractResource {

	public TemplateObjectModel() {
		super(SpiType.TEMPLATE_OBJECT_MODEL);
	}
	
	public static AbstractObjectModelSpiDelegate getDelegate() {
		return Singleton.get(AbstractObjectModelSpiDelegate.class);
	}
	
	public abstract String templateName();

	public final String toString() {

		// First, construct velocity context
		VelocityContext context = newVelocityContext();

		for (Field f : getClass().getDeclaredFields()) {

			boolean changedAccessibility = false;

			if (!f.isAccessible()) {
				changedAccessibility = true;
				f.setAccessible(true);
			}

			if (f.getAnnotation(Exclude.class) == null) {
				try {
					
					String name = f.getName();
					Object value = TemplateObjectModel.class.isAssignableFrom(f.getType()) ? f.get(this).toString()
							: f.get(this);
					
					context.put(name, value);

				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}

			if (changedAccessibility) {
				f.setAccessible(false);
			}
		}

		// Then, merge
		StringWriter w = new StringWriter();

		((Template) getTemplate()).merge(context, w);

		return w.toString();
	}

	private static VelocityContext newVelocityContext() {
		return new VelocityContext();
	}
	
	protected abstract Object getTemplate();

	protected abstract void setTemplate(Object template);

}
