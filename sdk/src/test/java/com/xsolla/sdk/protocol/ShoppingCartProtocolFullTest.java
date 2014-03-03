package com.xsolla.sdk.protocol;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.xsolla.sdk.exception.InvoiceNotFoundException;
import com.xsolla.sdk.exception.UnprocessableRequestException;
import com.xsolla.sdk.protocol.invoice.ShoppingCartProtocolInvoice;
import com.xsolla.sdk.protocol.request.Request;
import com.xsolla.sdk.protocol.response.Response;
import com.xsolla.sdk.protocol.storage.IPaymentShoppingCartStorage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.util.LinkedHashMap;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class ShoppingCartProtocolFullTest extends ProtocolFullTest {

    static final String PAY_V1_SUCCESS = "100";
    static final String PAY_V1_NOT_FOUND = "102";
    static final String PAY_V1_ANY_EXCEPTION = "103";
    static final String PAY_V1_UNPROCESSABLE = "104";
    static final String PAY_SHOP_ID = "AB12345";

    protected IPaymentShoppingCartStorage paymentShoppingCartStorageMock;

    protected ShoppingCartProtocol shoppingCartProtocol;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        this.paymentShoppingCartStorageMock = mock(IPaymentShoppingCartStorage.class);
        when(this.paymentShoppingCartStorageMock.pay(any(ShoppingCartProtocolInvoice.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                        ShoppingCartProtocolInvoice protocolInvoice = (ShoppingCartProtocolInvoice) invocationOnMock.getArguments()[0];
                        switch (protocolInvoice.getV1()) {
                            case PAY_V1_ANY_EXCEPTION:
                                throw new Exception("Any exception");
                            case PAY_V1_NOT_FOUND:
                                throw new InvoiceNotFoundException("Not found");
                            case PAY_V1_UNPROCESSABLE:
                                throw new UnprocessableRequestException("Unprocessable request");
                        }
                        return PAY_SHOP_ID;
                    }
                });
        this.addCancelHandler(this.paymentShoppingCartStorageMock);

        this.shoppingCartProtocol = this.protocolFactory.getShoppingCartProtocol(this.paymentShoppingCartStorageMock);
    }

    @Test
    @UseDataProvider("wrongCommandDataProvider")
    public void testWrongCommand(LinkedHashMap<String, String> params, String expectedXml) throws IOException, SAXException {
        Request requestMock = this.buildRequestMock(params);
        Response response = this.shoppingCartProtocol.run(requestMock);
        assertXMLEqual("Protocol ShoppingCart, command 'cancel'", expectedXml, response.getContent());
    }

    @DataProvider
    public static Object[][] wrongCommandDataProvider() {
        return new Object[][] {
                {
                        new LinkedHashMap<String, String>() {{
                            put("id", "100");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>20</result>" +
                            "<description>No command in request. Available commands are: 'pay', 'cancel'.</description>" +
                        "</response>"
                },
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "not_exist");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>20</result>" +
                            "<description>Wrong command 'not_exist'. Available commands for protocol ShoppingCart are: 'pay', 'cancel'.</description>" +
                        "</response>"
                }

        };
    }

    @Test
    @UseDataProvider("cancelDataProvider")
    public void testCancel(LinkedHashMap<String, String> params, String expectedXml) throws IOException, SAXException {
        Request requestMock = this.buildRequestMock(params);
        Response response = this.shoppingCartProtocol.run(requestMock);
        assertXMLEqual("Protocol ShoppingCart, command 'cancel'", expectedXml, response.getContent());
    }

    @Test
    @UseDataProvider("payShoppingCartDataProvider")
    public void testPayShoppingCart(LinkedHashMap<String, String> params, String expectedXml) throws IOException, SAXException {
        Request requestMock = this.buildRequestMock(params);
        Response response = this.shoppingCartProtocol.run(requestMock);
        assertXMLEqual("Protocol ShoppingCart, command 'pay'", expectedXml, response.getContent());
    }

    @DataProvider
    public static Object[][] payShoppingCartDataProvider() {
        return new Object[][] {
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "pay");
                            put("id", "100");
                            put("sign", "bc763ff5a8665c7c4c5fa0d8eba75ac8");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>20</result>" +
                            "<description>Invalid request format. Missed parameters: v1, amount, currency, datetime</description>" +
                        "</response>"
                },
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "pay");
                            put("id", "100");
                            put("v1", PAY_V1_SUCCESS);
                            put("amount", "100.20");
                            put("currency", "RUR");
                            put("datetime", "20130325184822");
                            put("sign", "11111ff5a8661171111111d8e1171111");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>20</result>" +
                            "<description>Invalid md5 signature</description>" +
                        "</response>"
                },
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "pay");
                            put("id", "555");
                            put("v1", PAY_V1_SUCCESS);
                            put("amount", "100.20");
                            put("currency", "RUR");
                            put("datetime", "DATETIME");
                            put("sign", "e15b464029164a011ed8b0eaf14e2fe8");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>20</result>" +
                            "<description>Date string 'DATETIME' does not match format 'YMdHms'</description>" +
                        "</response>"
                },
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "pay");
                            put("id", "555");
                            put("v1", PAY_V1_ANY_EXCEPTION);
                            put("amount", "100.20");
                            put("currency", "RUR");
                            put("datetime", "20130325184822");
                            put("sign", "413ee43a19ff875db28660f501d337f3");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>30</result>" +
                            "<description>Any exception</description>" +
                        "</response>"
                },
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "pay");
                            put("id", "555");
                            put("v1", PAY_V1_UNPROCESSABLE);
                            put("amount", "100.20");
                            put("currency", "RUR");
                            put("datetime", "20130325184822");
                            put("sign", "42f0ecd5a69e967fff56ae9f5a9ff021");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>40</result>" +
                            "<description>Unprocessable request</description>" +
                        "</response>"
                },
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "pay");
                            put("id", "555");
                            put("v1", PAY_V1_SUCCESS);
                            put("amount", "100.20");
                            put("currency", "RUR");
                            put("datetime", "20130325184822");
                            put("sign", "e15b464029164a011ed8b0eaf14e2fe8");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>0</result>" +
                            "<description/>" +
                            "<id_shop>AB12345</id_shop>" +
                        "</response>"
                }
        };
    }

}
