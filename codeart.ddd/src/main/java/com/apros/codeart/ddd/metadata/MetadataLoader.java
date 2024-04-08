package com.apros.codeart.ddd.metadata;

import static com.apros.codeart.i18n.Language.strings;

import java.util.ArrayList;

import com.apros.codeart.AppConfig;
import com.apros.codeart.ddd.DomainObject;
import com.apros.codeart.runtime.Activator;
import com.apros.codeart.util.ListUtil;

public final class MetadataLoader {
	private MetadataLoader() {
	}

	private static boolean loaded = false;

	private static Iterable<Class<?>> _domainTypes;

	public Iterable<Class<?>> domainTypes() {
		if (!loaded)
			throw new IllegalArgumentException(strings("codeart.ddd", "MetadataNotInitialized"));
		return _domainTypes;
	}

	/**
	 * 加载所有领域对象的元数据
	 */
	public static Iterable<Class<?>> load() {
		if (loaded)
			return _domainTypes;
		loaded = true;

		// 先构建动态领域对象的类型 todo

		// 之前些的代码，稍后构建动态领域对象类型可以用到，另外，除了动态构建领域对象外，还要根据配置，对已经存在的对象进行修改
//		// 从外部配置中得到
//		// 获得objectMetaCode
//		var objectMeta = DynamicObject.getMetadata(objectType.getSimpleName());
//		if (objectMeta != null)
//			DynamicProperty.register(objectType, objectMeta);

		// 在这里找出所有定义的领域对象
		_domainTypes = findDomainTypes();

		// 加载
		ObjectMetaLoader.load(_domainTypes);

		return _domainTypes;
	}

	private static Iterable<Class<?>> findDomainTypes() {
		var findedTypes = Activator.getSubTypesOf(DomainObject.class, AppConfig.mergeArchives("subsystem"));
		ArrayList<Class<?>> domainTypes = new ArrayList<>();
		for (var findedType : findedTypes) {
			if (ListUtil.contains(findedTypes, null))

				if (!ObjectMeta.isMergeDomainType(findedType)) {
					var exist = ListUtil.find(domainTypes, (t) -> {
						return t.getSimpleName().equalsIgnoreCase(findedType.getSimpleName());
					});
					if (exist != null) {
						throw new IllegalArgumentException(
								strings("codeart.ddd", "DomainSameName", findedType.getName(), exist.getName()));
					}
					domainTypes.add(findedType);
				}
		}
		return domainTypes;
	}

}
