package com.xsolla.sdk.paymentpage;

import com.xsolla.sdk.Invoice;
import com.xsolla.sdk.Project;
import com.xsolla.sdk.User;
import org.junit.Before;
import org.junit.Test;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

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
                .setEmail("email@example.com")
                .setPhone("user_phone")
                .setUserIP("user_userIp");
        this.invoice = new Invoice()
                .setCurrency("EUR")
                .setVirtualCurrencyAmount(new BigDecimal(1.11));
        this.project = new Project(7096, "KEY");
        this.urlBuilder = new UrlBuilder(project);
    }

    @Test
    public void testGetLink() throws NoSuchAlgorithmException {
        assertEquals(
                "https://secure.xsolla.com/paystation2/?project=7096&sign=094eb3c634f2612dead38608dc20eaec",
                this.urlBuilder.getUrl()
        );
    }

    @Test
    public void testGetLinkSandbox() throws NoSuchAlgorithmException {
        assertEquals(
                "https://sandbox-secure.xsolla.com/paystation2/?project=7096&sign=094eb3c634f2612dead38608dc20eaec",
                this.urlBuilder.getUrl(UrlBuilder.SANDBOX_URL)
        );
    }

    @Test
    public void testIgnoreBlankParameters() throws NoSuchAlgorithmException {
        this.urlBuilder.setParameter("description", "");
        this.testGetLink();
    }

    @Test
    public void testClear() throws NoSuchAlgorithmException {
        this.urlBuilder.setCountry("US").clear();
        this.testGetLink();
    }

    @Test
    public void testHiddenParameters() throws NoSuchAlgorithmException {
        this.urlBuilder.setInvoice(this.invoice);
        assertEquals(
                "https://secure.xsolla.com/paystation2/?currency=EUR&out=1.11&project=7096&sign=5bbc52cd72d7b3491025a7d6cca0cb70",
                this.urlBuilder.getUrl()
        );
    }

    /*
    @Test
    public void testClearSignedParameters() throws NoSuchAlgorithmException {
        this.urlBuilder.setUser(this.user, false, false);
        assertEquals(
                "https://secure.xsolla.com/paystation2/?email=email%40example.com" +
                        "&phone=user_phone&project=7096&sign=5b7c4eab43356844ec631771121277f5&signparams=allowSubscription%2Ccurrency%2C" +
                        "fastcheckout%2Cid_package%2Cout%2Cproject%2Csignparams%2Ctheme%2Cv0&" +
                        "userip=user_userIp&v1=user_v1&v2=user_v2&v3=user_v3",
                this.urlBuilder.getUrl()
        );
    }
    */


}
