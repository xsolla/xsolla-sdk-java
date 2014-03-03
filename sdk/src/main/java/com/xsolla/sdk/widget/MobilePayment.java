package com.xsolla.sdk.widget;

import com.xsolla.sdk.Project;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @link http://xsolla.github.io/en/mversion.html
 */
public class MobilePayment extends Paystation {

    public MobilePayment(Project project) {
        super(project);
    }

    public String getMarketplace() {
        return "landing";
    }

    public List<String> getRequiredParams() {
        return Arrays.asList("project", "pid", "marketplace");
    }

    public HashMap<String, String> getDefaultParams() {
        return new HashMap<String, String>() {{
            put("pid", "1738");
            put("theme", "201");
        }};
    }
}