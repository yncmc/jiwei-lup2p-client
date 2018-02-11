package com.jiwei.lup2p.client.page;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class TransferPage extends HTMLBase {

    private List<TransferProject> projects = new ArrayList<TransferProject>();

    public List<TransferProject> getProjects() {
        return projects;
    }

    public static TransferPage parse(String htmlText) {
        TransferPage result = new TransferPage();
        String[] strs = StringUtils.substringsBetween(htmlText, "<dd>", "class=\"ld-btn ld-btn-primary\">投资</a>");
        if (strs != null) {
            for (String str : strs) {
                TransferProject transferProject = TransferProject.parse(str);
                result.projects.add(transferProject);
            }
        }
        return result;
    }
}
