package com.jiwei.lup2p.client.page;

import static java.lang.Double.parseDouble;

import org.apache.commons.lang3.StringUtils;

public class Investment extends HTMLBase {

    private String name;
    private String interest;
    private String duration;
    private double amount;
    private double received;
    private double receiving;
    private String nextDate;
    private double nextAmount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInterest() {
        return interest;
    }

    public String getDuration() {
        return duration;
    }

    public double getAmount() {
        return amount;
    }

    public double getReceived() {
        return received;
    }

    public double getReceiving() {
        return receiving;
    }

    public String getNextDate() {
        return nextDate;
    }

    public void setNextDate(String nextDate) {
        this.nextDate = nextDate;
    }

    public double getNextAmount() {
        return nextAmount;
    }

    public static Investment parse(String row) {
        Investment result = new Investment();
        String[] values = StringUtils.substringsBetween(row, "<td", "</td>");

        String temp = StringUtils.substringAfter(values[0], "<br/>").trim().replace("\r\n", "").replace(" ", "");
        String[] strs = temp.split("\\|");
        result.interest = strs[0];
        result.duration = strs[1];

        result.name = StringUtils.substringBetween(values[0], ">", "</a>");
        result.name = StringUtils.substringAfter(result.name, ">").replace("\r\n", "").trim();

        result.amount = parseDouble(StringUtils.substringAfter(values[1], ">").trim().replace(",", ""));
        result.received = parseDouble(StringUtils.substringAfter(values[2], ">").trim().replace(",", ""));
        result.receiving = parseDouble(StringUtils.substringAfter(values[3], ">").trim().replace(",", ""));
        result.nextDate = StringUtils.substringAfter(values[4], ">").trim();
        result.nextAmount = parseDouble(StringUtils.substringBetween(values[5], "<span>", "</span>").trim().replace(",", ""));

        return result;
    }
}
