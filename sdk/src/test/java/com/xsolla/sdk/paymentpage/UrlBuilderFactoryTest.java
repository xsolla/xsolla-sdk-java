package com.xsolla.sdk.paymentpage;

import com.xsolla.sdk.Project;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import static org.junit.Assert.assertEquals;

public class UrlBuilderFactoryTest {

    protected Project project;
    protected UrlBuilderFactory urlBuilderFactory;

    @Before
    public void setUp() {
        this.project = new Project(7096, "KEY");
        this.urlBuilderFactory = new UrlBuilderFactory(this.project);
    }

    @Test
    public void testGetPayStation() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        assertEquals(
                "https://secure.xsolla.com/paystation2/?marketplace=paystation&project=7096&sign=094eb3c634f2612dead38608dc20eaec",
                this.urlBuilderFactory.getPayStation().getUrl()
        );
    }

    @Test
    public void testGetCreditCards() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        assertEquals(
                "https://secure.xsolla.com/paystation2/?marketplace=landing&pid=1380&project=7096&sign=457a3f06ea55593b6204b992963cf762&theme=201",
                this.urlBuilderFactory.getCreditCards().getUrl()
        );
    }

    @Test
    public void testGetPayDesk() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        assertEquals(
                "https://secure.xsolla.com/paystation2/?marketplace=paydesk&project=7096&sign=094eb3c634f2612dead38608dc20eaec",
                this.urlBuilderFactory.getPayDesk().getUrl()
        );
    }

    @Test
    public void testGetMobilePayment() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        assertEquals(
                "https://secure.xsolla.com/paystation2/?marketplace=landing&pid=1738&project=7096&sign=457a3f06ea55593b6204b992963cf762&theme=201",
                this.urlBuilderFactory.getMobilePayment().getUrl()
        );
    }

    @Test
    public void testGetDirectPayment() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        assertEquals(
                "https://secure.xsolla.com/paystation2/?marketplace=landing&pid=6&project=7096&sign=094eb3c634f2612dead38608dc20eaec",
                this.urlBuilderFactory.getDirectPayment("6").getUrl()
        );
    }

    @Test
    public void testGetMobileVersion() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        assertEquals(
                "https://secure.xsolla.com/paystation2/?marketplace=mobile&project=7096&sign=094eb3c634f2612dead38608dc20eaec",
                this.urlBuilderFactory.getMobileVersion().getUrl()
        );
    }
}
