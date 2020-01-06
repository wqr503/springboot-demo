package com.cn.lp.template;

import com.cn.lp.template.xml.XMLFactory;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TemplateSheetManager {

    public static final String GLOBAL_SHEET_NAME = "global.mvl";

    private Map<String, TemplateSheet> templateSheetMap = new HashMap<String, TemplateSheet>();

    public TemplateSheetManager(InputStream in, SheetSrcType type, SheetFilter filter) throws Exception {
        if (type == SheetSrcType.XML) {
            TemplateSheet templateSheet = XMLFactory.createTable(in);
            this.templateSheetMap.put(templateSheet.getName(), templateSheet);
        }
    }

    public Collection<TemplateSheet> getTemplateSheetList() {
        return Collections.unmodifiableCollection(templateSheetMap.values());
    }

    public TemplateSheet getTemplateSheetBy(String name) {
        return this.templateSheetMap.get(name);
    }

}
