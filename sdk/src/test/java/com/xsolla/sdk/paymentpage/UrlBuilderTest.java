package com.xsolla.sdk.paymentpage;

import com.xsolla.sdk.Invoice;
import com.xsolla.sdk.Project;
import com.xsolla.sdk.User;
import org.junit.Before;
import java.math.BigDecimal;

public class UrlBuilderTest {

    protected Project project;
    protected User user;
    protected Invoice invoice;
    protected UrlBuilder urlBuilder;

    @Before
    public void setUp() {
        this.user = new User("user_v1")
                .setV2("user_v2")
                .setV3("user_v3")
                .setEmail("example@example.com")
                .setPhone("79000000000")
                .setUserIP("1.2.3.4");
        this.invoice = new Invoice()
                .setCurrency("EUR")
                .setVirtualCurrencyAmount(new BigDecimal(1.11));
        this.project = new Project(7096, "KEY");
        this.urlBuilder = new UrlBuilder(project);
    }


}
