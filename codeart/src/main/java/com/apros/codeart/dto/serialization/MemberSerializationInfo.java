package com.apros.codeart.dto.serialization;

import java.lang.reflect.Field;

class MemberSerializationInfo {
//	#region 静态构造

	private static Class<?> getTargetClass(Class<?> classType, Field field) {
		if (classType != null)
			return classType;
		return field.getType();
	}

//	private static MemberSerializationInfo createByCollection(Class<?> targetClass, Field field, DTOMember memberAnn) {
//		if (is(targetClass, Iterable.class)) {
//			return field == null ? new CollectionSerializationInfo(targetClass)
//					: new CollectionSerializationInfo(field, memberAnn);
//		}
//
//		if (is(targetClass, Map.class)) {
//			throw new IllegalStateException("暂时不支持键值对的dto序列化操作"); // todo
//		}
//
//		return null;
//
//	}
//
//	public static MemberSerializationInfo Create(MemberInfo memberInfo, DTOMemberAttribute memberAttribute) {
//		Type t = GetTargetType(null, memberInfo);
//		// 数组
//		if (t.IsArray)
//			return new ArraySerializationInfo(memberInfo, memberAttribute);
//		// ICollection或IDictionary
//		MemberSerializationInfo info = CreateByCollection(t, memberInfo, memberAttribute);
//		if (info != null)
//			return info;
//		// 普通类型
//		return new MemberSerializationInfo(memberInfo, memberAttribute);
//	}
//
//	public static MemberSerializationInfo Create(Type classType) {
//		Type t = classType;
//		// 数组
//		if (t.IsArray)
//			return new ArraySerializationInfo(classType);
//		// ICollection或IDictionary
//		MemberSerializationInfo info = CreateByCollection(classType, null, null);
//		if (info != null)
//			return info;
//		// 普通类型
//		return new MemberSerializationInfo(classType);
//	}

//	#endregion

	private Field _field;

	public Field getField() {
		return _field;
	}

	private Class<?> _classType;

	private DTOMemberAnnotation _memberAnn;

	public DTOMemberAnnotation getMemberAnn() {
		return _memberAnn;
	}

	/**
	 * 字段名称
	 * 
	 * @return
	 */
	public String getName() {
		return _field.getName();
	}

	public Class<?> getOwnerClass() {
		if (_classType != null)
			return _classType;
		return _field.getDeclaringClass(); // 申明该字段的类
	}

	public boolean isAbstract() {
		return com.apros.codeart.runtime.Util.isAbstract(_field.getType());
	}

//	#region 序列化的目标

	public Class<?> getTargetClass() {
		return getTargetClass(_classType, _field);
	}

	public boolean isClassInfo() {
		return _classType != null;
	}

//	#endregion

	public MemberSerializationInfo(Class<?> classType) {
		_classType = classType;
	}

	public MemberSerializationInfo(Field field, DTOMemberAnnotation memberAnn) {
		_field = field;
		_memberAnn = memberAnn;
	}

//	/**
//	 * 生成序列化代码
//	 * 
//	 * @param g
//	 */
//	public void GenerateSerializeIL(MethodGenerator g)
//	{
//	    //serializer.serialize(v); 或 //writer.Writer(v);
//	    SerializationMethodHelper.Write(g, this.DTOMemberName, this.TargetType, (argType) =>
//	     {
//	         LoadMemberValue(g);
//	         //TargetType是成员（也就是属性或者字段）的类型
//	         //argType是方法需要接受到的类型，如果两者类型不匹配，就需要转换
//	         if (this.TargetType.IsStruct() && !argType.IsStruct())
//	         {
//	             g.Box();
//	         }
//	     });
//	}
//
//	/// <summary>
//	/// 加载成员的值到堆栈上
//	/// </summary>
//	protected void LoadMemberValue(MethodGenerator g) {
//		LoadOwner(g);
//
//		if (this.IsClassInfo)
//			return;
//		if (this.IsFieldInfo)
//			g.LoadField(this.FieldInfo);
//		else
//			g.LoadProperty(this.PropertyInfo);
//	}
//
//	/// <summary>
//	/// 生成反序列化代码
//	/// </summary>
//	/// <param name="g"></param>
//	public virtual void GenerateDeserializeIL(MethodGenerator g)
//	{
//	    SetMember(g, () =>
//	    {
//	        SerializationMethodHelper.Read(g, this.DTOMemberName, this.TargetType);
//	    });
//	}
//
//	public void SetMember(MethodGenerator g, Action loadValue) {
//		if (this.IsClassInfo) {
//			g.AssignVariable(SerializationArgs.InstanceName, loadValue);
//		} else {
//			LoadOwner(g);
//
//			if (this.IsFieldInfo) {
//				g.Assign(this.FieldInfo, loadValue);
//			} else {
//				g.Assign(this.PropertyInfo, loadValue);
//			}
//		}
//	}
//
//	private void LoadOwner(MethodGenerator g) {
//		if (this.OwnerType.IsValueType)
//			g.LoadVariable(SerializationArgs.InstanceName, LoadOptions.ValueAsAddress);
//		else
//			g.LoadVariable(SerializationArgs.InstanceName);
//	}
//
//	public string DTOMemberName
//	{
//	    get
//	    {
//	        if (this.MemberAttribute != null && !string.IsNullOrEmpty(this.MemberAttribute.Name)) return this.MemberAttribute.Name;
//	        if (!string.IsNullOrEmpty(_memberInfo.Name)) return _memberInfo.Name.FirstToUpper();
//	        return string.Empty;
//	    }
//	}
//
//	public virtual string
//
//	GetDTOSchemaCode()
//	{
//	    return this.DTOMemberName;
//	}
}