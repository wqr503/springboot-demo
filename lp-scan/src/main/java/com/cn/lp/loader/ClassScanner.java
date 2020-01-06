package com.cn.lp.loader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类扫描器
 */
public class ClassScanner {

    private static Logger logger = LoggerFactory.getLogger(ClassScanner.class);

    public static final int DEFAULT_CACHE_LIMIT = 256;

    public static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    /**
     * 资源解析器
     */
    private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    /**
     * 元消息读取器工厂
     */
    private static MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

    private List<ClassFilter> filters = new ArrayList<>();

    public static MetadataReaderFactory getReaderFactory() {
        return readerFactory;
    }

    public ClassLoader classLoader;

    public ClassScanner() {
    }

    public ClassScanner(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 添加过滤
     *
     * @param filters
     * @return
     */
    public ClassScanner addFilter(ClassFilter... filters) {
        for (ClassFilter fileFilter : filters) {
            this.filters.add(fileFilter);
        }
        return this;
    }

    /**
     * 添加过滤
     *
     * @param filters
     * @return
     */
    public ClassScanner addFilter(Collection<ClassFilter> filters) {
        this.filters.addAll(filters);
        return this;
    }

    /**
     * 是否过滤掉
     *
     * @param reader
     * @return
     */
    public boolean filter(MetadataReader reader) {
        for (ClassFilter fileFilter : this.filters) {
            if (!fileFilter.include(reader) || fileFilter.exclude(reader)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从包package中获取所有的Class(获取的Class中包含ClassScanner$1这种,如不需要可过滤)
     *
     * @param packs
     * @return
     */
    public Set<Class<?>> getClasses(String... packs) {

        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        // 是否循环迭代
        boolean recursive = true;
        for (String pack : packs) {
            // 获取包的名字 并进行替换
            String packageName = pack;
            String packageDirName = packageName.replace('.', '/');
            // 定义一个枚举的集合 并进行循环来处理这个目录下的things
            Enumeration<URL> dirs;
            try {
                if (classLoader == null) {
                    classLoader = Thread.currentThread().getContextClassLoader();
                }
                dirs = classLoader.getResources(packageDirName);
                logger.info("packageDirName : " + packageDirName);
                // 循环迭代下去
                while (dirs.hasMoreElements()) {
                    // 获取下一个元素
                    URL url = dirs.nextElement();
                    logger.info("url : " + URLDecoder.decode(url.getFile(), "UTF-8"));
                    // 得到协议的名称
                    String protocol = url.getProtocol();
                    // 如果是以文件的形式保存在服务器上
                    if ("file".equals(protocol)) {
                        // 获取包的物理路径
                        String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                        // 以文件的方式扫描整个包下的文件 并添加到集合中
                        this.findAndAddClassesInDirByFile(packageName, filePath, recursive, classes);
                    } else if ("jar".equals(protocol)) {
                        this.findAndAddClassesInJar(url, classes);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return classes;
    }

    private void findAndAddClassesInJar(URL url, Set<Class<?>> classes) throws IOException {
        UrlResource jarRes = null;
        if (StringUtils.endsWith(url.getPath(), "/")) {
            jarRes = new UrlResource(url);
        } else {
            jarRes = new UrlResource(url + "/");
        }
        URLConnection con = jarRes.getURL().openConnection();
        JarURLConnection jarCon = (JarURLConnection) con;
        JarFile jarFile = jarCon.getJarFile();
        JarEntry jarEntry = jarCon.getJarEntry();
        String rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
        if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
            rootEntryPath = rootEntryPath + "/";
        }
        for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
            JarEntry entry = entries.nextElement();
            String entryPath = entry.getName();
            if (entryPath.startsWith(rootEntryPath)) {
                String relativePath = entryPath.substring(rootEntryPath.length());
                if (relativePath.endsWith(".class")) {
                    Resource resource = jarRes.createRelative(relativePath);
                    if (!resource.isReadable()) {
                        continue;
                    }
                    MetadataReader reader = readerFactory.getMetadataReader(resource);
                    if (!this.filter(reader)) {
                        String fullClassName = reader.getClassMetadata().getClassName();
                        try {
                            classes.add(Thread.currentThread().getContextClassLoader().loadClass(fullClassName));
                        } catch (Throwable e) {
                            e.printStackTrace();
//                            LOG.error(LogUtils.format("添加用户自定义视图类错误 找不到此类的{}文件", fullClassName, e);
                        }
                    }
                }
            }
        }
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     * @throws IOException
     */
    private void findAndAddClassesInDirByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) throws IOException {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            logger.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                this.findAndAddClassesInDirByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                FileSystemResource resource = new FileSystemResource(file);
                MetadataReader reader = readerFactory.getMetadataReader(resource);
                // 如果是java类文件 去掉后面的.class 只留下类名
                try {
                    if (!this.filter(reader)) {
                        String fullClassName = reader.getClassMetadata().getClassName();
                        classes.add(Thread.currentThread().getContextClassLoader().loadClass(fullClassName));
                    }
                } catch (ClassNotFoundException e) {
                    logger.error("添加用户自定义视图类错误 找不到此类的.class文件", e);
                }
            }
        }
    }

//    public List<Resource> findClass(String basePackage) throws IOException {
//    	String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + "/" + DEFAULT_RESOURCE_PATTERN;
//		Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
//    	List<Resource> resourceList = new ArrayList<Resource>();
//    	for (Resource resource : resources) {
//    		resourceList.add(resource);
//    	}
//    	return resourceList;
//    }

    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(basePackage);
    }

}
