package com.xsolla.sdk.widget;

import com.xsolla.sdk.Project;

import java.util.Arrays;
import java.util.List;

/**
 * @link http://xsolla.github.io/en/paystation.html
 */
public class Paystation extends Widget {
    public Paystation(Project project) {
        super(project);
    }

    public String getMarketPlace() {
        return "paystation";
    }

    public List<String> getRequiredParams() {
        return Arrays.asList("project");
    }
}
