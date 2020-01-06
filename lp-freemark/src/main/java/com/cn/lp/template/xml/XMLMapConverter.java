package com.cn.lp.template.xml;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.*;

public class XMLMapConverter implements Converter {

	private static final String TYPE_NAME = "class";
	private static final String TYPE_LIST = "list";

	@Override
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class clazz) {
		return Map.class.isAssignableFrom(clazz) || List.class.isAssignableFrom(clazz);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		String classType = reader.getAttribute(TYPE_NAME);
		//		String nodeName = reader.getNodeName();
		//		Map<String, Object> objectMap = new HashMap<String, Object>();
		if (classType != null && classType.toLowerCase().equals(TYPE_LIST)) {
			List<Object> objectList = new ArrayList<Object>();
			while (reader.hasMoreChildren()) {
				reader.moveDown();
				Object object = doUnmarshal(reader);
				objectList.add(object);
				reader.moveUp();
			}
			return objectList;
			//			objectMap.put(nodeName, objectList);
		} else {
			return doUnmarshal(reader);
			//			objectMap.put(nodeName, object);
		}
		//		return objectMap;
	}

	private Object doUnmarshal(HierarchicalStreamReader reader) {
		String classType = reader.getAttribute(TYPE_NAME);
		if (classType != null && classType.toLowerCase().equals(TYPE_LIST)) {
			List<Object> objectList = new ArrayList<Object>();
			while (reader.hasMoreChildren()) {
				reader.moveDown();
				Object object = doUnmarshal(reader);
				objectList.add(object);
				reader.moveUp();
			}
			return objectList;
		} else {
			String nodeValue = reader.getValue();
			nodeValue = nodeValue.replace("\n", "").replace("\t", "");
			if (nodeValue != null && !nodeValue.equals("")) {
				if (nodeValue.toLowerCase().equals("null"))
					return null;
				else
					return nodeValue;
			}
			Map<String, Object> valueObject = new HashMap<String, Object>();
			for (Iterator<?> iterator = reader.getAttributeNames(); iterator.hasNext();) {
				String key = iterator.next().toString();
				String value = reader.getAttribute(key);
				if (value != null && value.toLowerCase().equals("null"))
					value = null;
				valueObject.put(key, value);
			}
			if (reader.hasMoreChildren()) {
				while (reader.hasMoreChildren()) {
					reader.moveDown();
					String key = reader.getNodeName();
					Object value = doUnmarshal(reader);
					valueObject.put(key, value);
					reader.moveUp();
				}
				//				objectMap.put(nodeName, valueObject);
				return valueObject;
			} else {
				return valueObject;
			}
		}
	}

}
