package com.jiwei.lup2p.client.page;

import org.apache.commons.lang3.StringUtils;

public class TransferProject extends HTMLBase {
    private String rate;
    private String duration;
    private String value;
    private String diff;
    private String nextDate;
    private String url;
    private String productId;

    public String getRate() {
        return rate;
    }

    public String getDuration() {
        return duration;
    }

    public String getValue() {
        return value;
    }

    public String getDiff() {
        return diff;
    }

    public String getNextDate() {
        return nextDate;
    }

    public String getUrl() {
        return url;
    }

    public String getProductId() {
        return productId;
    }

    public static TransferProject parse(String html) {
        TransferProject result = new TransferProject();
        String[] strs = StringUtils.substringsBetween(html, "<li ", "</li>");
        result.rate = StringUtils.substringBetween(strs[0], "\"num-style\">", "</p>").trim();
        result.duration = StringUtils.substringBetween(strs[1], "<p>", "</p>");
        result.value = StringUtils.substringBetween(strs[2], "\"collection-currency\">", "</span>");
        result.diff = StringUtils.substringBetween(strs[3], "<p>", "<span class=").trim();
        result.nextDate = StringUtils.substringBetween(html, "预计下一收款日：", "</span>").trim();
        result.url = "https://www.lup2p.com" + StringUtils.substringBetween(html, "<a href='", "' target=\"_blank\"");
        result.productId = StringUtils.substringAfter(result.url, "productId=");

        return result;
    }
}
