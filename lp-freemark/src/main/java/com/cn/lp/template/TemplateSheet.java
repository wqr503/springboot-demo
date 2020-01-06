package com.cn.lp.template;

import java.util.Map;

public interface TemplateSheet {

	public String getName();

	public boolean needOutPut();

	public String getOutput();

	public String getMvl();

	public Map<String, Object> getAttributeMap();

}
