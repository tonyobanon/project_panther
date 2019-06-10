package com.re.paas.internal.jvmtools.annotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.AnnotationsWriter;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;

public class ClassTools {

	public static <T extends Annotation> Annotation[] getAnnotations0(Executable m, Class<?> type) {
		@SuppressWarnings("unchecked")
		Annotation[] annotations = getAnnotations(m, (Class<T>) type);
		return annotations;
	}

	/**
	 * ImplNotes:<br>
	 * By default, this method does not check the classloader of the specified
	 * annotation type
	 * 
	 * @param m
	 * @param type
	 * @return
	 */
	public static <T extends Annotation> Annotation[] getAnnotations(Executable m, Class<T> type) {

		Annotation[] annotations = m.getAnnotations();

		List<Annotation> result = new ArrayList<>(annotations.length);

		Class<?> repeateableClass = type.getDeclaredAnnotation(Repeatable.class) != null
				? type.getDeclaredAnnotation(Repeatable.class).value()
				: null;

		if (annotations.length > 0) {
			for (Annotation a : annotations) {

				if (Proxy.isProxyClass(a.getClass())) {

					// ParserInvocationHandler iHandler = (ParserInvocationHandler)
					// Proxy.getInvocationHandler(a);

				}

				if (a.annotationType().getName().equals(type.getName())) {
					result.add(a);
				} else if (repeateableClass != null
						&& a.annotationType().getName().equals(repeateableClass.getName())) {
					@SuppressWarnings("unchecked")
					T[] value = (T[]) AnnotationParser.getAnnotationValues(a).get("value");
					for (T t : value) {
						result.add(t);
					}
				}
			}
		}

		return result.toArray(new Annotation[result.size()]);
	}

	public static Boolean isAnnotationPropertyTrue(Annotation a, String property) {
		return (Boolean) AnnotationParser.getAnnotationValues(a).get(property);
	}

	public static boolean hasAnnotation(Method m, Class<? extends Annotation> type) {
		return getAnnotations(m, type).length > 0;
	}

	public static <T extends Annotation> boolean annotationApplies(String typeName, Class<T> type) {

		Class<?> repeateableClass = type.getDeclaredAnnotation(Repeatable.class) != null
				? type.getDeclaredAnnotation(Repeatable.class).value()
				: null;

		if (typeName.equals(type.getName())
				|| (repeateableClass != null && typeName.equals(repeateableClass.getName()))) {
			return true;
		}
		return false;
	}

	public static void toAbstractMethod(CtClass source, Method method)
			throws NotFoundException, BadBytecode, CannotCompileException, IOException, ClassNotFoundException {

		Class<?>[] paramsTypes = method.getParameterTypes();
		List<CtClass> paramsTypesCT = new ArrayList<>();

		for (Class<?> cl : paramsTypes) {
			paramsTypesCT.add(source.getClassPool().get(cl.getName()));
		}

		CtMethod concreteMethod = source.getDeclaredMethod(method.getName(),
				paramsTypesCT.toArray(new CtClass[paramsTypesCT.size()]));

		CtMethod abstractMethod = new CtMethod(concreteMethod.getReturnType(), concreteMethod.getName(),
				concreteMethod.getParameterTypes(), concreteMethod.getDeclaringClass());

		source.removeMethod(concreteMethod);
		source.addMethod(abstractMethod);
	}

	public static void copyMethod(CtClass target, CtClass source, Method method,
			Class<? extends Annotation> annotationType)
			throws NotFoundException, BadBytecode, CannotCompileException, IOException, ClassNotFoundException {

		Class<?>[] paramsTypes = method.getParameterTypes();
		List<CtClass> paramsTypesCT = new ArrayList<>();

		for (Class<?> cl : paramsTypes) {
			paramsTypesCT.add(source.getClassPool().get(cl.getName()));
		}

		CtMethod sourceMethod = source.getDeclaredMethod(method.getName(),
				paramsTypesCT.toArray(new CtClass[paramsTypesCT.size()]));

		CtMethod newMethod = new CtMethod(sourceMethod.getReturnType(), sourceMethod.getName(),
				sourceMethod.getParameterTypes(), target);

		newMethod.setBody(sourceMethod, null);

		copyAnnotation(sourceMethod, newMethod, annotationType);

		target.addMethod(newMethod);
	}

	private static void copyAnnotation(CtMethod source, CtMethod target, Class<? extends Annotation> annotationType)
			throws IOException {

		AnnotationsAttribute sourceAnnotationsAttr = new AnnotationsAttribute(
				source.getDeclaringClass().getClassFile().getConstPool(), AnnotationsAttribute.visibleTag,
				source.getAttribute(AnnotationsAttribute.visibleTag));

		AnnotationsAttribute targetAnnotationsAttr = new AnnotationsAttribute(
				target.getDeclaringClass().getClassFile().getConstPool(), AnnotationsAttribute.visibleTag,
				target.getAttribute(AnnotationsAttribute.visibleTag));

		// First, determine annotations length
		int numAnnotations = targetAnnotationsAttr.get() != null ? targetAnnotationsAttr.numAnnotations() : 0;

		if (sourceAnnotationsAttr.get() != null) {
			for (javassist.bytecode.annotation.Annotation a : sourceAnnotationsAttr.getAnnotations()) {
				if (doesAnnotationApply(a.getTypeName(), annotationType)) {
					numAnnotations += 1;
				}
			}
		}

		// Then, write annotation bytes

		ByteArrayOutputStream output = new ByteArrayOutputStream();

		AnnotationsWriter writer = new AnnotationsWriter(output,
				target.getDeclaringClass().getClassFile().getConstPool());

		// Write annotation length
		writer.numAnnotations(numAnnotations);

		// Write existing annotation found on target
		if (targetAnnotationsAttr.get() != null) {
			for (javassist.bytecode.annotation.Annotation a2 : targetAnnotationsAttr.getAnnotations()) {
				a2.write(writer);
			}
		}

		if (sourceAnnotationsAttr.get() != null) {
			for (javassist.bytecode.annotation.Annotation a : sourceAnnotationsAttr.getAnnotations()) {
				if (doesAnnotationApply(a.getTypeName(), annotationType)) {
					copyAnnotation(a, writer);
				}
			}
		}

		writer.close();

		if (numAnnotations > 0) {

			byte[] attribute_info = output.toByteArray();

			targetAnnotationsAttr.set(attribute_info);

			// Add annotation to method info
			target.getMethodInfo().addAttribute(targetAnnotationsAttr);
		}
	}

	/**
	 * 
	 * @param target The target class to be transformed
	 * @param source The source class from where we read the annotation
	 * @param method The method from where we read the annotation
	 * @throws NotFoundException
	 * @throws BadBytecode
	 * @throws CannotCompileException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws Exception
	 */
	public static void copyAnnotation(CtClass target, CtClass source, Method method,
			Class<? extends Annotation> annotationType)
			throws NotFoundException, BadBytecode, CannotCompileException, IOException, ClassNotFoundException {

		// If this method is already annotated with annotationType, remove
		for (Annotation a : method.getDeclaredAnnotations()) {
			if (doesAnnotationApply(a.annotationType().getName(), annotationType)) {
				ClassTools.removeAnnotation(target, method, a.annotationType());
			}
		}

		Class<?>[] paramsTypes = method.getParameterTypes();
		List<CtClass> paramsTypesCT = new ArrayList<>();

		for (Class<?> cl : paramsTypes) {
			paramsTypesCT.add(source.getClassPool().get(cl.getName()));
		}

		CtMethod sourceMethod = source.getDeclaredMethod(method.getName(),
				paramsTypesCT.toArray(new CtClass[paramsTypesCT.size()]));

		CtMethod targetMethod = target.getDeclaredMethod(method.getName(),
				paramsTypesCT.toArray(new CtClass[paramsTypesCT.size()]));

		copyAnnotation(sourceMethod, targetMethod, annotationType);
	}

	private static void copyAnnotation(javassist.bytecode.annotation.Annotation a, AnnotationsWriter writer)
			throws IOException {

		writer.annotation("L" + a.getTypeName().replace(".", "/") + ";",
				a.getMemberNames() != null ? a.getMemberNames().size() : 0);

		if (a.getMemberNames() != null) {

			for (String name : a.getMemberNames()) {

				writer.memberValuePair(name); // element_value_pair

				MemberValue value = a.getMemberValue(name);

				if (value instanceof ArrayMemberValue) {

					ArrayMemberValue arrayValue = ((ArrayMemberValue) value);

					int num = arrayValue.getValue() == null ? 0 : arrayValue.getValue().length;
					writer.arrayValue(num);

					for (MemberValue v : arrayValue.getValue()) {

						if (v instanceof AnnotationMemberValue) {

							AnnotationMemberValue annotationValue = ((AnnotationMemberValue) v);

							writer.annotationValue();

							writer.annotation("L" + annotationValue.getValue().getTypeName().replace(".", "/") + ";",
									annotationValue.getValue().getMemberNames() != null
											? annotationValue.getValue().getMemberNames().size()
											: 0);

							if (annotationValue.getValue().getMemberNames() != null) {
								for (String n : annotationValue.getValue().getMemberNames()) {
									writer.memberValuePair(n);
									annotationValue.getValue().getMemberValue(n).write(writer);
								}
							}

						} else {
							v.write(writer);
						}
					}

				} else {
					value.write(writer);
				}
			}
		}
	}

	private static boolean doesAnnotationApply(String typeName, Class<? extends Annotation> annotationType) {

		Class<? extends Annotation> annotationListType = null;

		Repeatable repeatable = annotationType.getDeclaredAnnotation(Repeatable.class);
		if (repeatable != null) {
			annotationListType = repeatable.value();
		}

		return typeName.equals(annotationType.getName())
				|| (annotationListType != null && typeName.equals(annotationListType.getName()));
	}

	public static void removeAnnotation(CtClass clazz, Method method, Class<? extends Annotation> annotationType)
			throws NotFoundException, BadBytecode, CannotCompileException {

		// Create an array of CtClass instances for the method param types
		Class<?>[] paramsTypes = method.getParameterTypes();
		List<CtClass> paramsTypesCT = new ArrayList<>();

		for (Class<?> cl : paramsTypes) {
			paramsTypesCT.add(clazz.getClassPool().get(cl.getName()));
		}

		// Get annotation bytes from the source method
		CtMethod sourceMethod = clazz.getDeclaredMethod(method.getName(),
				paramsTypesCT.toArray(new CtClass[paramsTypesCT.size()]));

		// Removes the specified annotation type from sourceAnnotationsAttr

		AnnotationsAttribute sourceAnnotationsAttr = new AnnotationsAttribute(clazz.getClassFile().getConstPool(),
				AnnotationsAttribute.visibleTag, sourceMethod.getAttribute(AnnotationsAttribute.visibleTag));

		if (sourceAnnotationsAttr.get() != null) {
			while (sourceAnnotationsAttr.getAnnotation(annotationType.getName()) != null) {
				sourceAnnotationsAttr.removeAnnotation(annotationType.getName());
			}
		}

		// Add annotation to method info
		sourceMethod.getMethodInfo().addAttribute(sourceAnnotationsAttr);
	}

}
