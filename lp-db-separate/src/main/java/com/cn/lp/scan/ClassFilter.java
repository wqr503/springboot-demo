package com.cn.lp.scan;

import org.springframework.core.type.classreading.MetadataReader;

/**
 * 类过滤器
 */
public interface ClassFilter {

	/**
	 * 包含
	 * @param reader
	 * @return
	 */
    boolean include(MetadataReader reader);

    /**
     * 排除
     * @param reader
     * @return
     */
    boolean exclude(MetadataReader reader);

}