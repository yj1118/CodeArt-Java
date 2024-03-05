package com.apros.codeart.dto.serialization;

import com.apros.codeart.dto.DTObject;

class MarkupReader extends DTOReader {
	public MarkupReader(DTObject dto) {
		super(dto);
	}

//	public override T ReadElement<T>(
//	string name,
//	int index)
//	{
//	    var dtoList = _dto.GetList(name, false);
//	    if (dtoList == null) return default(T);
//	    var dtoElement = dtoList[index];
//	    if (dtoElement.IsSingleValue) return dtoElement.GetValue<T>();
//	    return (T)DTObjectDeserializer.Instance.Deserialize(typeof(T), dtoElement);
//	}

	@Override
	public Object readObject(Class<?> objectType, String name) {
		var dtoValue = _dto.getObject(name, null);
		if (dtoValue == null)
			return null;
		return DTObjectDeserializer.Instance.deserialize(objectType, dtoValue);
	}
}
