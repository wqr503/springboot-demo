package com.cn.lp.cmd;


import com.cn.lp.ConfigFileUtil;
import com.cn.lp.template.SheetFilter;
import com.cn.lp.template.TemplateSheetManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TemplateCmd {

    private static SheetFilter filter = new SheetFilter() {

        @Override
        public boolean accept(String sheetName) {
            if (sheetName.equals(TemplateSheetManager.GLOBAL_SHEET_NAME)) {
                return true;
            } else if ((sheetName.startsWith("s.") || sheetName.startsWith("c.")) && sheetName.endsWith("mvl")) {
                return true;
            }
            return false;
        }

    };

    private static final String BASE_PATH_KEY = "-basepath";

    private static final String DIR_KEY = "-dir";

    public static void main(String[] args) throws IOException {
        //		args = new String[] { "//Users//funsplay//Desktop//dto//DemandResultListDTO.xml" };
        List<String> fileList = new ArrayList<>();
        Properties properties = new Properties();
        for (String arg : args) {
            if (arg.startsWith("-")) {
                String[] property = arg.split("=");
                if (property.length < 2)
                    throw new IllegalArgumentException(arg + " is error!");
                properties.put(property[0], property[1]);
            } else {
                fileList.add(arg);
            }
        }
        properties.put(DIR_KEY, "true");
        fileList.add("C:\\workspace\\source\\workspace\\lp-demo\\lp-freemark\\src\\main\\resources");
        for (String filePath : fileList) {
            try {
                boolean dir = properties.getProperty(DIR_KEY) != null ? Boolean.valueOf(properties.getProperty(DIR_KEY)) : false;
                if (dir) {
                    ConfigFileUtil.dealConfigDir(new File(filePath),  properties.getProperty(BASE_PATH_KEY), filter);
                } else {
                    ConfigFileUtil.dealConfigFile(new File(filePath), properties.getProperty(BASE_PATH_KEY), filter);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
