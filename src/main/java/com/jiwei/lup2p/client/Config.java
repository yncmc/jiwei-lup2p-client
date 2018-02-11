package com.jiwei.lup2p.client;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Config {

    private static final Log log = LogFactory.getLog(Config.class);

    private String userName;
    private String password;

    private int count;

    private long intervalMills;

    private int transferMinAmount;
    private int transferMaxAmount;

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public int getCount() {
        return count;
    }

    public long getIntervalMills() {
        return intervalMills;
    }

    public int getTransferMinAmount() {
        return transferMinAmount;
    }

    public int getTransferMaxAmount() {
        return transferMaxAmount;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).toString();
    }

    private static Config current;

    public static Config getCurrent() {
        return current;
    }

    public static void init() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config.ini"));
        }
        catch (Throwable e) {
            throw new Error("加载配置文件出错: " + e.getMessage());
        }
        Config config = new Config();
        config.userName = properties.getProperty("userName");
        config.password = properties.getProperty("password");
        config.count = Integer.parseInt(properties.getProperty("count"));
        config.transferMinAmount = Integer.parseInt(properties.getProperty("transfer.minAmount"));
        config.transferMaxAmount = Integer.parseInt(properties.getProperty("transfer.maxAmount"));
        config.intervalMills = Long.parseLong(properties.getProperty("interval.mills"));

        Config.current = config;

        log.info(config);
    }
}
