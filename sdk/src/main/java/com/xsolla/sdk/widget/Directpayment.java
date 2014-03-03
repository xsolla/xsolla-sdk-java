package com.xsolla.sdk.widget;

import com.xsolla.sdk.Project;

import java.util.Arrays;
import java.util.List;

/**
 * @link http://xsolla.github.io/en/directpayment.html
 */
public class Directpayment extends Paystation {

    public Directpayment(Project project) {
        super(project);
    }

    public String getMarketplace() {
        return "landing";
    }

    public List<String> getRequiredParams() {
        return Arrays.asList("project", "pid", "marketplace");
    }
}