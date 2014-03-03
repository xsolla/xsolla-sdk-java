package com.xsolla.sdk.paymentpage;

import com.xsolla.sdk.Project;
import java.util.HashMap;

public class UrlBuilderFactory {

    protected Project project;

    public UrlBuilderFactory(Project project) {
        this.project = project;
    }

    /**
     * @link http://xsolla.github.io/en/paystation.html
     * @return UrlBuilder
     */
    public UrlBuilder getPayStation() {
        return new UrlBuilder(this.project, new HashMap<String, String>() {{
            put("marketplace", "paystation");
        }});
    }

    /**
     * @link http://xsolla.github.io/en/card.html
     * @return UrlBuilder
     */
    public UrlBuilder getCreditCards() {
        return new UrlBuilder(this.project, new HashMap<String, String>() {{
            put("marketplace", "landing");
            put("pid", "1380");
            put("theme", "201");
        }});
    }

    /**
     * @link http://xsolla.github.io/en/pswidget.html
     * @return UrlBuilder
     */
    public UrlBuilder getPayDesk() {
        return new UrlBuilder(this.project, new HashMap<String, String>() {{
            put("marketplace", "paydesk");
        }});
    }

    /**
     * @link http://xsolla.github.io/en/mversion.html
     * @return UrlBuilder
     */
    public UrlBuilder getMobilePayment() {
        return new UrlBuilder(this.project, new HashMap<String, String>() {{
            put("marketplace", "landing");
            put("theme", "201");
            put("pid", "1738");
        }});
    }

    /**
     * @link http://xsolla.github.io/en/directpayment.html
     * @param pid payment system ID
     * @return UrlBuilder
     */
    public UrlBuilder getDirectPayment(final String pid) {
        return new UrlBuilder(this.project, new HashMap<String, String>() {{
            put("marketplace", "landing");
            put("pid", pid);
        }});
    }

    /**
     * @link http://xsolla.github.io/en/mversion.html
     * @return UrlBuilder
     */
    public UrlBuilder getMobileVersion() {
        return new UrlBuilder(this.project, new HashMap<String, String>() {{
            put("marketplace", "mobile");
        }});
    }


}
