package com.jiwei.lup2p.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jiwei.lup2p.client.page.Investment;
import com.jiwei.lup2p.client.page.LoginPage;
import com.jiwei.lup2p.client.page.MyInvestmentPage;
import com.jiwei.lup2p.client.page.TransferPage;
import com.jiwei.lup2p.client.page.TransferProject;

public class ClientMain {

    private static final Log log = LogFactory.getLog(ClientMain.class);

    private static final String COOKIES_PATH = "./cookies";

    public static void main(String[] args) throws Throwable {
        Config.init();

        File cookieFolder = new File(COOKIES_PATH);
        if (args.length >= 1 && args[0].equals("-force")) {
            FileUtils.forceDelete(cookieFolder);
        }

        HttpClient client = new HttpClient();
        if (cookieFolder.exists()) {
            HttpState state = new HttpState();
            state.addCookies(CookieUtils.read(cookieFolder));
            client.setState(state);
            MyInvestmentPage investmentPage = openInvestmentPage(client);
            log.debug("已投入笔数: " + investmentPage.getInvestments().size());

            if (investmentPage.getInvestments().isEmpty()) {
                doLogin(client);
            }
        }
        else {
            doLogin(client);
        }
        doTransfer(client);
    }

    private static MyInvestmentPage openInvestmentPage(HttpClient client) throws Throwable {
        GetMethod request = new GetMethod("https://www.lup2p.com/lup2p/my/investment-all");
        client.executeMethod(request);
        String pageText = readString(request.getResponseBodyAsStream());
        // log.debug(pageText);
        String tableText = StringUtils.substringBetween(pageText, "<tbody>", "</tbody>");
        // log.debug(tableText);
        String[] rows = StringUtils.substringsBetween(tableText, "<tr", "</tr>");

        MyInvestmentPage page = new MyInvestmentPage();
        if (rows != null) {
            for (String row : rows) {
                Investment investment = Investment.parse(row);
                page.getInvestments().add(investment);
            }
        }
        return page;
    }

    private static void doTransfer(HttpClient client) throws Throwable {
        TransferPage page = null;
        int i = Config.getCurrent().getCount();
        while (i-- >= 1) {
            StringBuilder sb = new StringBuilder("https://www.lup2p.com/lup2p/transfer/list/p2p?");
            sb.append("minMoney=" + Config.getCurrent().getTransferMinAmount());
            sb.append("maxMoney=" + Config.getCurrent().getTransferMaxAmount());
            sb.append("&minDays=&maxDays=&minRate=&maxRate=&mode=&tradingMode=&isOverdueTransfer=0&isCx=&currentPage=1&orderCondition=&isShared=&canRealized=&productCategoryEnum=CGI&notHasBuyFeeRate=");
            GetMethod request = new GetMethod(sb.toString());
            client.executeMethod(request);
            String html = readString(request.getResponseBodyAsStream());
            page = TransferPage.parse(html);
            if (page.getProjects().size() > 0) {
                break;
            }
            log.debug("查询转让信息:" + i);
            Thread.sleep(Config.getCurrent().getIntervalMills());
            keepOnline(client);
        }

        if (page != null && page.getProjects().size() > 0) {
            TransferProject project = page.getProjects().get(0);
            log.debug(project);
            GetMethod projectDetailRequest = new GetMethod(project.getUrl());
            client.executeMethod(projectDetailRequest);
            log.info("详细转让信息");
            log.info(readString(projectDetailRequest.getResponseBodyAsStream()));
            log.info("准备提交");
            GetMethod investRequest = new GetMethod("https://www.lup2p.com/trading/i-trade-info?from=lup2p&sid=&productId=" + project.getProductId());
            client.executeMethod(investRequest);
            String html = readString(investRequest.getResponseBodyAsStream());
            log.debug(html);
        }
    }

    private static long count = 0;
    private static void keepOnline(HttpClient client) throws Throwable {
        MyInvestmentPage page = openInvestmentPage(client);
        assert page.getInvestments().size() > 0;
        if (count++ % 10 == 0) {
            log.info("保存登录状态");
            CookieUtils.write(client.getState().getCookies(), COOKIES_PATH);
        }
    }

    private static void doLogin(HttpClient client) throws Throwable {
        GetMethod get = new GetMethod("https://www.lup2p.com/user/login");
        client.executeMethod(get);
        String loginHtml = readString(get.getResponseBodyAsStream());
        LoginPage loginPage = LoginPage.parse(loginHtml);
        log.debug(loginPage);

        saveVCodeJpg(client);

        log.debug("需要登录，请输入验证码:");
        Scanner scanner = new Scanner(System.in);
        String vcode = scanner.nextLine();
        scanner.close();

        // ScriptEngineManager manager = new ScriptEngineManager();
        // String js = FileUtils.readFileToString(new File("lu_rsa.js"), "UTF-8");
        //
        // ScriptEngine engine = manager.getEngineByName("javascript");
        // engine.eval(js);
        // Invocable invocable = (Invocable)engine;
        // invocable.invokeFunction("RSASetPublic", new Object[] {loginPage.getPublicKey(), loginPage.getRsaExponent()});
        // if (args.length == 0) {
        // return;
        // }

        PostMethod loginRequest = new PostMethod("https://www.lup2p.com/user/login");
        loginRequest.addParameter("userName", Config.getCurrent().getUserName());
        loginRequest.addParameter("password", Config.getCurrent().getPassword());
        loginRequest.addParameter("isTrust", "Y");
        loginRequest.addParameter("loginFlag", "2");
        loginRequest.addParameter("validNum", vcode);

        int status = client.executeMethod(loginRequest);
        log.debug("status: " + status);

        String resultText = readString(loginRequest.getResponseBodyAsStream());
        if (resultText.indexOf("验证码错误") > 0) {
            throw new Error("验证码错误");
        }
        log.debug(resultText);
        CookieUtils.write(client.getState().getCookies(), COOKIES_PATH);
    }

    private static void saveVCodeJpg(HttpClient client) throws IOException, HttpException, FileNotFoundException {
        GetMethod get = new GetMethod("https://www.lup2p.com/user/captcha/captcha.jpg?source=login&_=" + System.currentTimeMillis());
        client.executeMethod(get);
        InputStream inputStream = get.getResponseBodyAsStream();
        FileOutputStream fos = new FileOutputStream(new File("vcode.jpg"));
        int len = 0;
        byte[] buffer = new byte[1024];
        while ((len = inputStream.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }
        fos.close();
        inputStream.close();
    }

    private static String readString(InputStream stream) throws Throwable {
        BufferedReader bis = new BufferedReader(new InputStreamReader(stream));
        String str = "";
        StringBuilder sb = new StringBuilder();
        while ((str = bis.readLine()) != null) {
            sb.append(str).append("\r\n");
        }
        return sb.toString();
    }
}
