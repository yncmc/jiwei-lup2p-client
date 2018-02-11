package com.jiwei.lup2p.client;

import org.apache.commons.lang3.StringUtils;

public class HtmlParseUtils {
    public static String findInputElement(String htmlText, String name) {
        String str = StringUtils.substringAfter(htmlText, "name=\"" + name + "\"");
        str = StringUtils.substringAfter(str, "value=");
        return StringUtils.substringBetween(str, "\"");
    }
}
