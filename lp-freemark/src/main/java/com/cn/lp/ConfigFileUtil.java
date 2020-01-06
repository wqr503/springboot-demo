package com.cn.lp;

import com.cn.lp.template.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigFileUtil {

    /**
     * 模板后缀
     */
    //	private static final String TMP_SUFFIX = ".mvl";

    private static final String logFileName = "log.txt";

//    protected ParserContext parserContext = new ParserContext();

    private static Logger logger = LoggerFactory.getLogger(ConfigFileUtil.class);

    public static void dealConfigDir(File dirPath, String basePath, SheetFilter filter) throws Exception {
        if (!dirPath.isDirectory()) {
            throw new RuntimeException(dirPath + "不是文件夹");
        }
        for (File file : dirPath.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".xml")) {
                try {
                    dealConfigFile(file, basePath, filter);
                } catch (Exception e) {
                    //
                }
            } else if (file.isDirectory()) {
                dealConfigDir(file, basePath, filter);
            }
        }
    }

    public static void dealConfigFile(File config, String basePath, SheetFilter filter) throws Exception {
        InputStream in = new FileInputStream(config);
        File dir = basePath != null ? new File(basePath) : config.getParentFile();
        List<String> errorList = new ArrayList<>();
        try (in) {
            SheetSrcType type = SheetSrcType.getByFile(config);
            TemplateSheetManager excelManager = new TemplateSheetManager(in, type, filter);
            TemplateSheet paramSheet = excelManager.getTemplateSheetBy(TemplateSheetManager.GLOBAL_SHEET_NAME);
            for (TemplateSheet sheet : excelManager.getTemplateSheetList()) {
                if (!sheet.needOutPut()) {
                    continue;
                }
                try {
                    // step1 创建freeMarker配置实例
                    Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
                    cfg.setSharedVariable(FormatDirective.DIRECTIVE_NAME, new FormatDirective());
                    cfg.setDefaultEncoding("UTF-8");
                    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

                    logger.info(dir.getAbsolutePath());
                    cfg.setDirectoryForTemplateLoading(dir);

                    //---数据源
                    Map<String, Object> attributeMap = new HashMap<>(sheet.getAttributeMap());
                    if (paramSheet != null) {
                        attributeMap.putAll(paramSheet.getAttributeMap());
                    }
                    Template template = cfg.getTemplate(sheet.getMvl());
                    File exportFile = new File(dir.getAbsolutePath() + sheet.getOutput());
                    if (!exportFile.getParentFile().exists()) {
                        exportFile.getParentFile().mkdirs();
                    }
                    Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(exportFile)));
                    try (out) {
                        template.process(attributeMap, out);
                        System.out.println("文件创建成功 !");
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    errorList.add("处理Excel文件[" + config.getName() + "]的子表[" + sheet.getName() + "]出错，错误：\n" + e.getMessage());
                    log(e);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            log(e);
            throw new Exception(e);
        } finally {
            if (!errorList.isEmpty()) {
                throw new TemplateException(errorList.toString());
            }
        }
    }

    private static void log(Throwable e) {
        PrintStream print = null;
        try {
            print = new PrintStream(new FileOutputStream(logFileName, true), false, "utf-8");
            e.printStackTrace(print);
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            IOUtils.closeQuietly(print);
        }
    }

    //	private static String convertPath(String sheetName) {
    //		String[] parts = StringUtils.split(sheetName, '.');
    //		if (parts.length == 2) {
    //			return sheetName;
    //		}
    //		String temp = StringUtils.join(Arrays.copyOf(parts, parts.length - 1), '/');
    //		return temp + "." + parts[parts.length - 1];
    //	}
}
