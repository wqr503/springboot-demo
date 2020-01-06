package com.cn.lp.template.xml;


import com.cn.lp.template.TemplateSheet;
import com.cn.lp.template.TemplateSheetConfig;

import java.util.Collections;
import java.util.Map;

public class XMLTable implements TemplateSheet {

	private TemplateSheetConfig config;

	private Map<String, Object> attributeMap;

	public TemplateSheetConfig getConfig() {
		return config;
	}

	@Override
	public String getName() {
		return "xml";
	}

	@Override
	public boolean needOutPut() {
		return true;
	}

	@Override
	public String getOutput() {
		return config.getOutput();
	}

	@Override
	public String getMvl() {
		return config.getMvl();
	}

	@Override
	public Map<String, Object> getAttributeMap() {
		return Collections.unmodifiableMap(attributeMap);
	}

}
