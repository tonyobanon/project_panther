package com.re.paas.internal.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.google.common.collect.Lists;
import com.re.paas.api.annotations.develop.PlatformInternal;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.templating.TemplateObjectModel;
import com.re.paas.api.templating.TemplateObjectModelFactory;
import com.re.paas.internal.classes.ClassUtil;

/**
 * This model used Apache Velocity as the default templating engine
 */
public class TemplatingModel extends BaseModel {

	@Override
	public String path() {
		return "core/emailing_templating";
	}

	/**
	 * This method transforms the given template, by recursively invoking the
	 * corresponding factory on all nested fields (defined and inherited) of type
	 * TemplateObjectModel
	 * 
	 */
	@PlatformInternal
	public static TemplateObjectModel getTemplate(TemplateObjectModel template) {
		try {
			return getTemplate0(template);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static boolean transform(TemplateObjectModel template) {

		// Look for a suitable factory to transform this object
		for (Entry<Class<?>, TemplateObjectModelFactory<? extends TemplateObjectModel>> e : TemplateObjectModelFactory
				.getDelegate().resources().entrySet()) {

			Class<?> clazz = e.getKey();
			@SuppressWarnings("unchecked")
			TemplateObjectModelFactory<TemplateObjectModel> factory = (TemplateObjectModelFactory<TemplateObjectModel>) e
					.getValue();

			if (!clazz.isAssignableFrom(template.getClass())) {
				continue;
			}

			template = factory.create(template);
			return true;
		}

		return false;
	}

	private static void transform(TemplateObjectModel template, Field f)
			throws IllegalArgumentException, IllegalAccessException {

		if (!(TemplateObjectModel.class.isAssignableFrom(f.getType()) &&

		// We will not transform null fields. This is to ensure that all required
		// instance variables has been properly initialized by the template, and the
		// constructor has been duly invoked. The factory can then do it's work from
		// there..

				f.get(template) != null)) {
			return;
		}

		// Look for a suitable factory to transform this field
		for (Entry<Class<?>, TemplateObjectModelFactory<? extends TemplateObjectModel>> e : TemplateObjectModelFactory
				.getDelegate().resources().entrySet()) {

			Class<?> clazz = e.getKey();
			@SuppressWarnings("unchecked")
			TemplateObjectModelFactory<TemplateObjectModel> factory = (TemplateObjectModelFactory<TemplateObjectModel>) e
					.getValue();

			if (!clazz.isAssignableFrom(f.getType())) {
				continue;
			}

			TemplateObjectModel model = factory.create((TemplateObjectModel) f.get(template));

			f.set(template, model);

		}
	}

	private static TemplateObjectModel getTemplate0(TemplateObjectModel template)
			throws IllegalArgumentException, IllegalAccessException {

		assert template != null;

		transform(template);

		Class<?> modelClass = template.getClass();

		List<Field> fields = Lists.newArrayList();

		// Add declared fields
		for (Field f : modelClass.getDeclaredFields()) {
			fields.add(f);
		}

		// Add a field consumer to emit warning messages if fields of type
		// TemplateObjectModel do not have the correct access level
		Consumer<Field> fieldConsumer = (f -> {
			if (TemplateObjectModel.class.isAssignableFrom(f.getType())
					&& (!Modifier.isProtected(f.getModifiers()) || Modifier.isPublic(f.getModifiers()))) {
				Logger.get().warn("Field: " + f.getName() + " in " + f.getClass().getName()
						+ " is not public or protected. It will not be inherited to: " + modelClass);
			}
		});

		// Add inherited fields
		for (Field f : ClassUtil.getInheritedFields(modelClass, TemplateObjectModel.class, fieldConsumer)) {
			fields.add(f);
		}

		for (Field f : fields) {

			boolean changedAccessibility = false;

			if (!f.isAccessible()) {
				changedAccessibility = true;
				f.setAccessible(true);
			}

			transform(template, f);

			if (changedAccessibility) {
				f.setAccessible(false);
			}

		}

		return template;
	}

	static {

	}

}
