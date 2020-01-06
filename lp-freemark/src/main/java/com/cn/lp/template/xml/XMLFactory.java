package com.cn.lp.template.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class XMLFactory {

	public static XMLTable createTable(InputStream input) {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
		StringBuilder builder = new StringBuilder();
		String line = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				builder.append(line.trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		XMLMapConverter xmlMapConverter = new XMLMapConverter();
		XStream xStream = new XStream(new DomDriver());
		xStream.alias("table", XMLTable.class);
		xStream.registerLocalConverter(XMLTable.class, "attributeMap", xmlMapConverter);
		XMLTable table = (XMLTable) xStream.fromXML(builder.toString());
		//		System.out.println(JSONUtils.toJson(table));
		return table;
	}

}
