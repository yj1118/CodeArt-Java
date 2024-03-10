package com.apros.codeart.dto.serialization;

import java.util.Comparator;
import java.util.HashMap;

import com.apros.codeart.dto.DTObject;
import com.apros.codeart.util.StringUtil;

class SchemaCodes implements Comparator {

	/**
	 * 成员名称 -> 架构代码
	 */
	private HashMap<String, String> _codes;

	/// <summary>
	/// 类型->架构代码
	/// </summary>
	private HashMap<Class<?>, String> _typeCodes;

	private DTObject _schema;

	private boolean _containsAll;

	public boolean containsAll() {
		return _containsAll;
	}

	public SchemaCodes(Class<?> classType, String schemaCode) {
		_codes = new HashMap<String, String>();
		_typeCodes = new HashMap<Class<?>, String>();

		_containsAll = StringUtil.isNullOrEmpty(schemaCode);
		_schema = DTObject.readonly(schemaCode);
		initialize(classType, schemaCode);
	}

	private boolean tryAddTypeCode(Class<?> classType, String schemaCode) {
		if (!_typeCodes.containsKey(classType)) {
			_typeCodes.put(classType, schemaCode); // 为当前对象类型建立 类型->架构代码的映射
			return true;
		}
		return false;
	}

	private void Initialize(Class<?> classType, String schemaCode) {
		tryAddTypeCode(classType, schemaCode); // 为当前对象类型建立 类型->架构代码的映射

		var fields = classType.getDeclaredFields();
		for (var field : fields) {
			var entity = _schema.find(field.getName());
			if (entity != null) {
				var memberType = member.GetFieldOrPropertyType();
				CollectSchemaCode(entity, memberType);
			}
		}
	}

	private void CollectSchemaCode(DTEntity entity, Type objectType)
	{
	    if (DataUtil.IsPrimitiveType(objectType))
	    {
	        _codes.Add(entity.Name, entity.GetSchemaCode(false, true));
	        return;
	    }

	    //以下是处理集合和对象的类型
	    if (objectType.IsList())
	    {
	        var elementType = objectType.ResolveElementType();
	        var sc = entity.GetSchemaCode(false, true);
	        var pos = sc.IndexOf(":");
	        if (pos > -1)
	        {
	            //对于集合类型找出成员的架构代码
	            var elementSC = sc.Substring(pos + 1).TrimStart("[").TrimEnd("]"); //定义了架构代码
	            _codes.Add(entity.Name, elementSC);
	            //记忆一次
	            TryAddTypeCode(elementType, elementSC);
	            return;
	        }


	        string code = null;
	        //没有定义架构代码，先尝试从类型代码中获取
	        if (_typeCodes.TryGetValue(elementType, out code))
	        {
	            _codes.Add(entity.Name, code);
	        }
	        else
	        {
	            _codes.Add(entity.Name, string.Empty); //定义空的架构，代表全部属性
	        }
	    }
	    else
	    {
	        //Object的处理
	        var sc = entity.GetSchemaCode(false, true);
	        var pos = sc.IndexOf(":");
	        if (pos > -1)
	        {
	            sc = sc.Substring(pos + 1); //定义了架构代码
	            _codes.Add(entity.Name, sc);
	            //记忆一次
	            TryAddTypeCode(objectType, sc);
	            return;
	        }


	        string code = null;
	        //没有定义架构代码，先尝试从类型代码中获取
	        if (_typeCodes.TryGetValue(objectType, out code))
	        {
	            _codes.Add(entity.Name, code);
	        }
	        else
	        {
	            _codes.Add(entity.Name, string.Empty); //定义空的架构，代表全部属性
	        }
	    }
	}

	public bool CanMarkup(string memberName) {
		if (this.ContainsAll)
			return true;
		return _schema.FindEntity(memberName, false) != null;
	}

	public string GetSchemaCode(string memberName, Func<Type> getObjectType)
	{
	    if (this.ContainsAll) return string.Empty; //空字符串代表取所有成员
	    string code = null;
	    if (_codes.TryGetValue(memberName, out code)) return code;
	    string typeCode = null;
	    if (_typeCodes.TryGetValue(getObjectType(), out typeCode)) return typeCode;
	    return null;
	}

	public void Sort(List<MemberSerializationInfo> members) {
		members.Sort(this);
	}

	private int GetMemberIndex(MemberSerializationInfo member)
	{
	    int index = 0;
	    var es = _schema._root.GetEntities();
	    foreach (var e in es)
	    {
	        if (e.Name.EqualsIgnoreCase(member.Name)) return index;
	        index++;
	    }
	    return index;
	}

	public int Compare(MemberSerializationInfo x, MemberSerializationInfo y) {
		return GetMemberIndex(x).CompareTo(GetMemberIndex(y));
	}
}
