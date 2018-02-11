package com.jiwei.lup2p.client.page;

import com.jiwei.lup2p.client.HtmlParseUtils;

public class LoginPage extends HTMLBase {

    private String publicKey;
    private String rsaExponent;

    private LoginPage() {
    }

    public String getPublicKey() {
        return publicKey;
    }

    private void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getRsaExponent() {
        return rsaExponent;
    }

    private void setRsaExponent(String rsaExponent) {
        this.rsaExponent = rsaExponent;
    }

    public static LoginPage parse(String loginHtml) {
        LoginPage result = new LoginPage();
        result.setPublicKey(HtmlParseUtils.findInputElement(loginHtml, "publicKey"));
        result.setRsaExponent(HtmlParseUtils.findInputElement(loginHtml, "rsaExponent"));
        return result;
    }
}
