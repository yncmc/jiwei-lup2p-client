package com.lup2p.client.test;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.jiwei.lup2p.client.page.TransferPage;

public class TransferPageTest {

    private static final Log log = LogFactory.getLog(TransferPageTest.class);

    @Test
    public void test() throws Throwable {
        String htmlText = FileUtils.readFileToString(new File("./pages/transfer.html"), "UTF-8");
        TransferPage page = TransferPage.parse(htmlText);
        log.debug(page);
    }
}
