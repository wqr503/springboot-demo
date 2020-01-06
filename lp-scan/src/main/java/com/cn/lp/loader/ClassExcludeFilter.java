package com.cn.lp.loader;

import org.springframework.core.type.classreading.MetadataReader;

/**
 * 类排除过滤器
 */
public abstract class ClassExcludeFilter implements ClassFilter {

	@Override
	public boolean include(MetadataReader reader) {
		return true;
	}
	
}
