package com.cn.lp.scan;

import org.springframework.core.type.classreading.MetadataReader;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 注解类过滤器
 */
public class AnnotationClassFilter implements ClassFilter {

    private Set<Class<? extends Annotation>> includes = new HashSet<>();

    private Set<Class<? extends Annotation>> excludes = new HashSet<>();

    /**
     * 生成包含该注解的过滤器
     * @param includes
     * @return
     */
    public static ClassFilter ofInclude(Collection<Class<? extends Annotation>> includes) {
        return new AnnotationClassFilter(includes, null);
    }

    /**
     * 生成包含该注解的过滤器
     * @param includes
     * @return
     */
    @SuppressWarnings("unchecked")
	public static ClassFilter ofInclude(Class<? extends Annotation>... includes) {
        return new AnnotationClassFilter(Arrays.asList(includes), null);
    }

    /**
     * 生成排除该注解的过滤器
     * @param excludes
     * @return
     */
    public static ClassFilter ofExclude(Collection<Class<? extends Annotation>> excludes) {
        return new AnnotationClassFilter(excludes, null);
    }

    /**
     * 生成排除该注解的过滤器
     * @param excludes
     * @return
     */
    @SuppressWarnings("unchecked")
	public static ClassFilter ofExclude(Class<? extends Annotation>... excludes) {
        return new AnnotationClassFilter(Arrays.asList(excludes), null);
    }

    public static ClassFilter of(Collection<Class<? extends Annotation>> includes, Collection<Class<? extends Annotation>> excludes) {
        return new AnnotationClassFilter(includes, excludes);
    }

    private AnnotationClassFilter(
            Collection<Class<? extends Annotation>> includes,
            Collection<Class<? extends Annotation>> excludes) {
        if (includes != null) {
            this.includes.addAll(includes);
        }
        if (excludes != null) {
            this.excludes.addAll(excludes);
        }
    }

    @Override
    public boolean include(MetadataReader reader) {
        return ClassFilterHelper.matchAnnotation(reader, this.includes);
    }

    @Override
    public boolean exclude(MetadataReader reader) {
        return ClassFilterHelper.matchAnnotation(reader, this.excludes);
    }

}
