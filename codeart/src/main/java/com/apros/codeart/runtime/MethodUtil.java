package com.apros.codeart.runtime;

import static com.apros.codeart.runtime.Util.propagate;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Function;

import com.apros.codeart.util.LazyIndexer;
import com.apros.codeart.util.ListUtil;
import com.apros.codeart.util.Memoized;
import com.apros.codeart.util.StringUtil;

/**
 * 
 */
public final class MethodUtil {
	private MethodUtil() {
	}

	public static Method get(String fullMethodName) {
		try {
			var lastDot = fullMethodName.lastIndexOf(".");
			var className = StringUtil.substr(fullMethodName, 0, lastDot);
			var methodName = StringUtil.substr(fullMethodName, lastDot + 1);
			var cls = Class.forName(className);
			return cls.getMethod(methodName);
		} catch (Exception ex) {
			throw propagate(ex);
		}
	}

	private static Function<Class<?>, Function<String, Iterable<Method>>> _getMethods = LazyIndexer.init((objCls) -> {
		return LazyIndexer.init((methodName) -> {
			return findMethods(objCls, methodName);
		});
	});

	private static Iterable<Method> findMethods(Class<?> objCls, String methodName) {
		Method[] methods = objCls.getDeclaredMethods();

		return ListUtil.filter(methods, (m) -> {
			return m.getName().equals(methodName);
		});
	}

	/**
	 * resolve得到的方法，如果没有，则返回null,不会报错
	 * 
	 * @param objCls
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	@Memoized
	public static Method resolve(Class<?> objCls, String methodName, Class<?>[] parameterTypes, Class<?> returnType) {

		var methods = _getMethods.apply(objCls).apply(methodName);

		for (var method : methods) {
			if (method.getParameterCount() != parameterTypes.length) {
				continue;
			}
			var argTypes = method.getParameterTypes();
			boolean finded = true;
			for (var i = 0; i < argTypes.length; i++) {
				if (!argTypes[i].equals(parameterTypes[i])) {
					finded = false;
					break;
				}
			}

			if (!method.getReturnType().equals(returnType))
				finded = false;
			if (finded)
				return method;
		}
		return null;
	}

	/**
	 * 忽略返回值得到方法
	 * 
	 * @param objCls
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	@Memoized
	public static Method resolveSlim(Class<?> objCls, String methodName, Class<?>... parameterTypes) {

		var methods = _getMethods.apply(objCls).apply(methodName);

		for (var method : methods) {
			if (method.getParameterCount() != parameterTypes.length) {
				continue;
			}
			var argTypes = method.getParameterTypes();
			boolean finded = true;
			for (var i = 0; i < argTypes.length; i++) {
				if (!argTypes[i].equals(parameterTypes[i])) {
					finded = false;
					break;
				}
			}
			if (finded)
				return method;
		}
		return null;
	}

	@Memoized
	public static Method resolveByName(Class<?> objCls, String methodName) {

		var methods = _getMethods.apply(objCls).apply(methodName);
		return ListUtil.first(methods);
	}

	public static Method resolveByNameOnce(Class<?> objCls, String methodName) {
		var methods = findMethods(objCls, methodName);
		return ListUtil.first(methods);
	}

	public static boolean isStatic(Method method) {
		return Modifier.isStatic(method.getModifiers());
	}

	public static boolean isPublic(Method method) {
		return Modifier.isPublic(method.getModifiers());
	}

}
