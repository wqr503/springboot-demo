package com.cn.lp.template;

import freemarker.core.Environment;
import freemarker.template.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created by on 2019/7/16.
 */
public class FormatDirective implements TemplateDirectiveModel {

    public final static String DIRECTIVE_NAME = "format";

    @Override
    @SuppressWarnings("rawtypes")
    public void execute(Environment env, Map params, TemplateModel[] loopVars,
        TemplateDirectiveBody body) throws TemplateException, IOException {
        if (body == null) {
            return;//do nothing
        }
        String whiteSpace = this.getRequiredParam(params, "blank");
        int blank = Integer.parseInt(whiteSpace);
        if (blank == 0) {
            body.render(env.getOut());
            return;//do nothing
        }
        StringWriter writer = new StringWriter();
        body.render(writer);
        String results = this.format(writer.toString(), blank);
        if (results != null) {
            env.getOut().write(results);
        }
    }

    /**
     * 格式化缩进
     *
     * @param source 源,可以是多行的文本
     * @param blank  空格数量。
     *               0：直接返回source;
     *               大于0：在每一行的左边增加blank个空格;
     *               小于0:在每一行的左边删除(<=)blank个空格
     * @return
     * @throws IOException
     */
    public String format(String source, int blank) throws IOException {
        if (source == null) {
            return null;
        }

        StringBuffer append = null;
        if (blank == 0) {
            return source;
        } else if (blank > 0) {
            append = new StringBuffer();
            for (int i = 0; i < blank; i++) {
                append.append(" ");
            }
        }

        StringReader stringReader = new StringReader(source);
        BufferedReader reader = new BufferedReader(stringReader);

        StringBuffer buf = new StringBuffer();
        String line = null;
        int lineNum = 0;//行号
        String sep = System.getProperty("line.separator");

        while ((line = reader.readLine()) != null) {
            lineNum++;
            //处理换行符
            if (lineNum >= 2) {//第2行开始，先增加一个换行符号
                buf.append(sep);
            }
            //处理行
            if (blank > 0) {//增加缩进
                buf.append(append).append(line);
            } else {//减少缩进
                int beginIndex = 0;
                int len = line.length();
                StringBuffer spaces = new StringBuffer();
                for (int i = 0; i < blank * -1; i++) {
                    if (i >= len) {
                        break;
                    }
                    char c = line.charAt(i);
                    if (c == ' ') {
                        beginIndex++;
                        spaces.append(" ");
                    } else if (c == '\t') {
                        beginIndex++;
                        spaces.append("    ");
                        i += 3;
                    } else {
                        break;
                    }
                }
                if (beginIndex > 0) {
                    if (spaces.length() > blank * -1) {
                        buf.append(spaces.delete(0, blank * -1));
                    }
                    buf.append(line.substring(beginIndex));
                } else {
                    buf.append(line);
                }
            }
        }
        return buf.toString();
    }

    @SuppressWarnings("rawtypes")
    String getRequiredParam(Map params, String key) throws TemplateException {
        Object value = params.get(key);
        if (value == null || value.toString().trim().length() == 0) {
            throw new TemplateModelException("not found required parameter:"
                + key + " for directive " + this.toString());
        }
        return value.toString();
    }

    @Override
    public String toString() {
        return "<@" + DIRECTIVE_NAME + ">";
    }

}
