package com.cn.lp.scan;

import org.springframework.core.type.classreading.MetadataReader;

/**
 * 类包含过滤器
 */
public abstract class ClassIncludeFilter implements ClassFilter {

    @Override
	public boolean exclude(MetadataReader reader) {
        return false;
    }
	
}
