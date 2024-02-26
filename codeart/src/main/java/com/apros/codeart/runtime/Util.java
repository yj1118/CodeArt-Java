package com.apros.codeart.runtime;

import java.lang.reflect.Modifier;

public class Util {
	@SuppressWarnings("unchecked")
	public static <T> T as(Object obj, Class<T> cls) {
		if (cls.isInstance(obj))
			return (T) obj;
		return null;
	}

	public static boolean is(Object obj, Class<?> cls) {
		return cls.isInstance(obj);
	}

	public static boolean any(Object obj, Class<?>... clses) {
		for (var cls : clses) {
			if (cls.isInstance(obj))
				return true;
		}
		return false;
	}

	public static boolean is(Class<?> cls, Class<?> targetCls) {
		return targetCls.isAssignableFrom(cls);
	}

	public static RuntimeException propagate(Throwable throwable) {
		return new RuntimeException(throwable);
	}

	public static boolean isAbstract(Class<?> cls) {
		return Modifier.isAbstract(cls.getModifiers());
	}
}