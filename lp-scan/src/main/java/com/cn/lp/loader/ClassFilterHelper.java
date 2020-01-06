package com.cn.lp.loader;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 类过滤器帮手
 *
 * @author wqr
 */
public abstract class ClassFilterHelper {

    /**
     * 构建包含类过滤器
     *
     * @param predicate
     * @return
     */
    public static ClassIncludeFilter ofInclude(final Predicate<MetadataReader> predicate) {

        return new ClassIncludeFilter() {

            @Override
            public boolean include(MetadataReader reader) {
                return predicate.test(reader);
            }

        };
    }

    /**
     * 构建排除类过滤器
     *
     * @param predicate
     * @return
     */
    public static ClassExcludeFilter ofExclude(final Predicate<MetadataReader> predicate) {

        return new ClassExcludeFilter() {

            @Override
            public boolean exclude(MetadataReader reader) {
                return predicate.test(reader);
            }

        };
    }

    /**
     * 是否匹配注解
     *
     * @param reader
     * @param annotations
     * @return
     */
    public static boolean matchAnnotation(MetadataReader reader, Class<? extends Annotation>... annotations) {
        List<Class<? extends Annotation>> annos = Arrays.asList(annotations);
        return matchAnnotation(reader, annos);
    }

    /**
     * 是否匹配注解
     *
     * @param reader
     * @param annotations
     * @return
     */
    public static boolean matchAnnotation(MetadataReader reader, Iterable<Class<? extends Annotation>> annotations) {
        for (Class<? extends Annotation> clazz : annotations) {
            AnnotationMetadata annotationMetadata = reader.getAnnotationMetadata();
            Set<String> annotationStrings = annotationMetadata.getAnnotationTypes();
            if (annotationStrings.contains(clazz.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否匹配子类
     *
     * @param reader
     * @param classes
     * @return
     */
    public static boolean matchSuper(MetadataReader reader, Class<?>... classes) {
        return matchSuper(reader, Arrays.asList(classes));
    }


    /**
     * 是否匹配子类
     *
     * @param reader
     * @param classes
     * @return
     */
    public static boolean matchSuper(MetadataReader reader, Collection<Class<?>> classes) {
        try {
            String superName = reader.getClassMetadata().getSuperClassName();
            for (Class<?> data : classes) {
                if (data.getName().equals(superName)) {
                    return true;
                }
            }
            List<String> interfaceNames = Arrays.asList(reader.getClassMetadata().getInterfaceNames());
            for (Class<?> data : classes) {
                if (interfaceNames.contains(data.getName())) {
                    return true;
                }
            }
            MetadataReaderFactory factory = ClassScanner.getReaderFactory();
            for (String name : interfaceNames) {
                MetadataReader interfaceReader = factory.getMetadataReader(name);
                if (matchSuper(interfaceReader, classes)) {
                    return true;
                }
            }
            if (superName == null || superName.equals(Object.class.getName())) {
                return false;
            }
            MetadataReader superClassReader = factory.getMetadataReader(superName);
            if (matchSuper(superClassReader, classes)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
