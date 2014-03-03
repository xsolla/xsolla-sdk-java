package com.xsolla.sdk.widget;

import com.xsolla.sdk.Project;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @link http://xsolla.github.io/en/card.html
 */
public class CreditCards extends Paystation {

    public CreditCards(Project project) {
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
            put("pid", "1380");
            put("theme", "201");
        }};
    }
}
