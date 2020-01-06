package com.cn.lp.template;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum SheetSrcType {

	XML("xml"),

	EXCEL("excel", "xls", "xlsx");

	private List<String> suffixList;

	private SheetSrcType(String... suffixs) {
		this.suffixList = Collections.unmodifiableList(Arrays.asList(suffixs));
	}

	public List<String> getSuffixList() {
		return suffixList;
	}

	public static SheetSrcType getByFile(File file) {
		String extensionName = FilenameUtils.getExtension(file.getName()).toLowerCase();
		for (SheetSrcType type : SheetSrcType.values()) {
			if (type.suffixList.contains(extensionName))
				return type;
		}
		return null;
	}

}
