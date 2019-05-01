package com.re.paas.internal.jvmtools.annotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.annotation.AnnotationsWriter;

public class AnnotationUtil {

	/**
	 * ImplNotes:<br>
	 * By default, this method does not check the classloader of the specified
	 * annotation type
	 * 
	 * @param m
	 * @param type
	 * @return
	 */
	public static Annotation[] getAnnotations(Executable m, Class<? extends Annotation> type) {

		Annotation[] annotations = m.getAnnotations();
		List<Annotation> result = new ArrayList<>(annotations.length);

		if (annotations.length > 0) {
			for (Annotation a : annotations) {
				if (a.toString().startsWith("@" + type.getName())) {
					result.add(a);
				}
			}
		}

		return result.toArray(new Annotation[result.size()]);
	}

	public static boolean hasAnnotation(Method m, Class<? extends Annotation> type) {
		return getAnnotations(m, type).length > 0;
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
	 * @throws Exception
	 */
	public static void copyAnnotation(CtClass target, CtClass source, Method method,
			Class<? extends Annotation> annotationType)
			throws NotFoundException, BadBytecode, CannotCompileException, IOException {

		// If this method is already annotated with annotationType, remove
		for (Annotation a : method.getDeclaredAnnotations()) {
			if (a.annotationType().getName().equals(annotationType.getName())) {
				AnnotationUtil.removeAnnotation(target, method, a.annotationType());
			}
		}

		// Create an array of CtClass instances for the method param types
		Class<?>[] paramsTypes = method.getParameterTypes();
		List<CtClass> paramsTypesCT = new ArrayList<>();

		for (Class<?> cl : paramsTypes) {
			paramsTypesCT.add(source.getClassPool().get(cl.getName()));
		}

		// Get annotation bytes from the source method
		CtMethod sourceMethod = source.getDeclaredMethod(method.getName(),
				paramsTypesCT.toArray(new CtClass[paramsTypesCT.size()]));

		CtMethod targetMethod = target.getDeclaredMethod(method.getName(),
				paramsTypesCT.toArray(new CtClass[paramsTypesCT.size()]));

		AnnotationsAttribute sourceAnnotationsAttr = new AnnotationsAttribute(source.getClassFile().getConstPool(),
				AnnotationsAttribute.visibleTag, sourceMethod.getAttribute(AnnotationsAttribute.visibleTag));

		AnnotationsAttribute targetAnnotationsAttr = new AnnotationsAttribute(target.getClassFile().getConstPool(),
				AnnotationsAttribute.visibleTag, targetMethod.getAttribute(AnnotationsAttribute.visibleTag));

		// First, determine annotations length
		int numAnnotations = targetAnnotationsAttr.get() != null ? targetAnnotationsAttr.numAnnotations() : 0;

		if (sourceAnnotationsAttr.get() != null) {
			for (javassist.bytecode.annotation.Annotation a : sourceAnnotationsAttr.getAnnotations()) {
				if (a.getTypeName().equals(annotationType.getName())) {
					numAnnotations += 1;
				}
			}
		}

		// Then, write annotation bytes

		ByteArrayOutputStream output = new ByteArrayOutputStream();

		AnnotationsWriter writer = new AnnotationsWriter(output, target.getClassFile().getConstPool());

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

				if (a.getTypeName().equals(annotationType.getName())) {

					writer.annotation("L" + annotationType.getName().replace(".", "/") + ";",
							a.getMemberNames() != null ? a.getMemberNames().size() : 0);

					if (a.getMemberNames() != null) {
						for (String name : a.getMemberNames()) {
							writer.memberValuePair(name); // element_value_pair
							a.getMemberValue(name).write(writer);
						}
					}
				}
			}
		}

		writer.close();

		if (numAnnotations > 0) {

			byte[] attribute_info = output.toByteArray();
			targetAnnotationsAttr.set(attribute_info);

			// Add annotation to method info
			targetMethod.getMethodInfo().addAttribute(targetAnnotationsAttr);

		}
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
