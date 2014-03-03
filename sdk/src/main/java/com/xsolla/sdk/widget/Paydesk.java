package com.xsolla.sdk.widget;

import com.xsolla.sdk.Project;

import java.util.Arrays;
import java.util.List;


public class Paydesk extends Paystation {

    public Paydesk(Project project) {
        super(project);
    }

    public String getMarketplace() {
        return "landing";
    }

    public List<String> getRequiredParams() {
        return Arrays.asList("project", "pid", "marketplace");
    }
}