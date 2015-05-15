package co.pishfa.accelerate.reflection;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class ReflectionUtils {

    //TODO need a real cache here
    private static Map<String, List<Field>> fieldCache = new HashMap<>();
	public static List<Field> getAllFieldsAnnotatedWith(Class<?> cls, Class<? extends Annotation> annotationClass) {
        String key = cls.getName() + annotationClass.getName();

		List<Field> fields = fieldCache.get(key);
        if(fields == null) {
            fields = new ArrayList<>();
            addAllFieldsAnnotatedWith(cls, annotationClass, fields);
            fieldCache.put(key, fields);
        }
		return fields;
	}

	private static void addAllFieldsAnnotatedWith(Class<?> cls, Class<? extends Annotation> annotationClass,
			List<Field> fields) {
		if (cls == null || cls == Object.class) {
			return;
		}

		addAllFieldsAnnotatedWith(cls.getSuperclass(), annotationClass, fields);
		for (Field field : cls.getDeclaredFields()) {
			if (field.isAnnotationPresent(annotationClass)) {
				fields.add(field);
			}
		}
	}

    //TODO need a real cache here
    private static Map<String, List<Method>> methodsCache = new HashMap<>();
	public static List<Method> getAllMethodsAnnotatedWith(Class<?> cls, Class<? extends Annotation> annotationClass) {
        String key = cls.getName() + annotationClass.getName();

		List<Method> methods = methodsCache.get(key);
        if(methods == null) {
            methods = new ArrayList<>();
            addAllMethodsAnnotatedWith(cls, annotationClass, methods);
            methodsCache.put(key, methods);
        }
		return methods;
	}

	private static void addAllMethodsAnnotatedWith(Class<?> cls, Class<? extends Annotation> annotationClass,
			List<Method> methods) {
		if (cls == null || cls == Object.class) {
			return;
		}

		addAllMethodsAnnotatedWith(cls.getSuperclass(), annotationClass, methods);
		for (Method method : cls.getDeclaredMethods()) {
			if (method.isAnnotationPresent(annotationClass)) {
				methods.add(method);
			}
		}
	}

	/**
	 * @return the annotation presents on this class or one of its superclasses (even if the annotation is not
	 *         inherited)
	 */
	public static <A extends Annotation> A getDerivedAnnotation(Class<?> clazz, Class<A> annotation) {
		if (clazz == null || clazz == Object.class)
			return null;
		A res = clazz.getAnnotation(annotation);
		if (res != null)
			return res;
		return getDerivedAnnotation(clazz.getSuperclass(), annotation);
	}

	public static ParameterizedType getParameterizedSuperClass(Class<?> clazz) {
		if (clazz == null || clazz == Object.class)
			return null;
		if (!(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
			return getParameterizedSuperClass(clazz.getSuperclass());
		}
		return (ParameterizedType) clazz.getGenericSuperclass();
	}

	/**
	 * @return the annotation presents directly, inherited, or annotation of another annotation
	 */
	public static <A extends Annotation> A getAnnotation(AccessibleObject obj, Class<A> annotationClass) {
		A annotation = obj.getAnnotation(annotationClass);
		if (annotation != null)
			return annotation;

		// search through annotations
		for (Annotation a : obj.getAnnotations()) {
			annotation = getDerivedAnnotation(a.annotationType(), annotationClass);
			if (annotation != null)
				return annotation;
		}

		return null;
	}

    public static String getMethodPropertyName(String name) {
        if(name.startsWith("get")) {
            name = name.substring("get".length());
        } else if(name.startsWith("is")) {
            name = name.substring("is".length());
        }
        return StringUtils.uncapitalize(name);
    }

}
