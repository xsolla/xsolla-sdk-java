package com.xsolla.sdk.protocol;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.xsolla.sdk.User;
import com.xsolla.sdk.exception.*;
import com.xsolla.sdk.exception.SecurityException;
import com.xsolla.sdk.protocol.invoice.StandardProtocolInvoice;
import com.xsolla.sdk.protocol.request.Request;
import com.xsolla.sdk.protocol.response.Response;
import com.xsolla.sdk.protocol.storage.IPaymentStandardStorage;
import com.xsolla.sdk.protocol.storage.IUserStorage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class StandardProtocolFullTest extends ProtocolFullTest {

    static final long PAY_ID_SUCCESS = 100;
    static final long PAY_ID_UNPROCESSABLE = 102;
    static final long PAY_ID_ANY_EXCEPTION = 103;
    static final String CHECK_V1_SUCCESS = "demo";
    static final String CHECK_V1_NOT_EXISTS = "unknown";
    static final String CHECK_V1_ANY_EXCEPTION = "exception";

    protected IPaymentStandardStorage paymentStandardStorageMock;
    protected IUserStorage userStorageMock;

    protected StandardProtocol standardProtocol;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        this.paymentStandardStorageMock = mock(IPaymentStandardStorage.class);
        when(this.paymentStandardStorageMock.pay(any(StandardProtocolInvoice.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                        StandardProtocolInvoice protocolInvoice = (StandardProtocolInvoice) invocationOnMock.getArguments()[0];
                        if (protocolInvoice.getXsollaPaymentId() == PAY_ID_ANY_EXCEPTION) {
                            throw new Exception("Any exception");
                        } else if (protocolInvoice.getXsollaPaymentId() == PAY_ID_UNPROCESSABLE) {
                            throw new UnprocessableRequestException("Unprocessable exception");
                        }
                        return "123100";
                    }
                });
        this.addCancelHandler(this.paymentStandardStorageMock);

        this.userStorageMock = mock(IUserStorage.class);
        when(this.userStorageMock.isUserExists(any(User.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                        User user = (User) invocationOnMock.getArguments()[0];
                        switch (user.getV1()) {
                            case CHECK_V1_ANY_EXCEPTION:
                                throw new Exception("Any exception");
                            case CHECK_V1_NOT_EXISTS:
                                return false;
                        }
                        return true;
                    }
                });
        when(this.userStorageMock.getAdditionalUserFields(any(User.class)))
                .thenReturn(new HashMap<String, String>() {{
                    put("parameter1", "value1");
                    put("parameter2", "value2");
                }});

        this.standardProtocol = this.protocolFactory.getStandardProtocol(this.userStorageMock, this.paymentStandardStorageMock);
    }

    /*
    @Test
    @UseDataProvider("ipCheckerDataProvider")
    public void testIpChecker(LinkedHashMap<String, String> params, String expectedXml) throws IOException, SAXException {
        doThrow(new SecurityException("You IP address is not in white list."))
                .when(this.ipCheckerMock).checkIp(eq(CLIENT_IP));
        Request requestMock = this.buildRequestMock(params);
        Response response = this.standardProtocol.run(requestMock);
        assertXMLEqual("Standard protocol, command 'cancel'", expectedXml, response.getContent());
    }

    @DataProvider
    public static Object[][] ipCheckerDataProvider() {
        return new Object[][] {
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "pay");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>7</result>" +
                            "<comment>You IP address is not in white list.</comment>" +
                        "</response>"
                }

        };
    }
    */

    @Test
    @UseDataProvider("wrongCommandDataProvider")
    public void testWrongCommand(LinkedHashMap<String, String> params, String expectedXml) throws IOException, SAXException {
        Request requestMock = this.buildRequestMock(params);
        Response response = this.standardProtocol.run(requestMock);
        assertXMLEqual("Standard protocol, command 'cancel'", expectedXml, response.getContent());
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
                            "<result>4</result>" +
                            "<comment>No command in request. Available commands are: 'check', 'pay', 'cancel'.</comment>" +
                        "</response>"
                },
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "not_exist");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>4</result>" +
                            "<comment>Wrong command 'not_exist'. Available commands for Standard protocol are: 'check', 'pay', 'cancel'.</comment>" +
                        "</response>"
                }

        };
    }

    @Test
    @UseDataProvider("cancelDataProvider")
    public void testCancel(LinkedHashMap<String, String> params, String expectedXml) throws IOException, SAXException {
        Request requestMock = this.buildRequestMock(params);
        Response response = this.standardProtocol.run(requestMock);
        assertXMLEqual("Standard protocol, command 'cancel'", expectedXml, response.getContent());
    }

    @Test
    @UseDataProvider("checkDataProvider")
    public void testCheck(LinkedHashMap<String, String> params, String expectedXml) throws IOException, SAXException {
        Request requestMock = this.buildRequestMock(params);
        Response response = this.standardProtocol.run(requestMock);
        assertXMLEqual("Standard protocol, command 'check'", expectedXml, response.getContent());
    }

    @DataProvider
    public static Object[][] checkDataProvider() {
        return new Object[][] {
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "check");
                            put("v1", CHECK_V1_ANY_EXCEPTION);
                            put("md5", "22a0964fb976608f25f2d55ee44c01d3");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>1</result>" +
                            "<comment>Any exception</comment>" +
                        "</response>"
                },
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "check");
                            put("v1", CHECK_V1_NOT_EXISTS);
                            put("md5", "7c7fce6806ae451419dede822033dd9e");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>7</result>" +
                            "<comment/>" +
                        "</response>"
                },
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "check");
                            put("v1", CHECK_V1_SUCCESS);
                            put("md5", "a3561b90df78828133eb285e36965419");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>0</result>" +
                            "<comment/>" +
                            "<specification>" +
                                "<parameter1>value1</parameter1>" +
                                "<parameter2>value2</parameter2>" +
                            "</specification>" +
                        "</response>"
                },
        };
    }

    @Test
    @UseDataProvider("payStandardDataProvider")
    public void testPayStandard(LinkedHashMap<String, String> params, String expectedXml) throws IOException, SAXException {
        Request requestMock = this.buildRequestMock(params);
        Response response = this.standardProtocol.run(requestMock);
        assertXMLEqual("Standard protocol, command 'pay'", expectedXml, response.getContent());
    }

    @DataProvider
    public static Object[][] payStandardDataProvider() {
        return new Object[][] {
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "pay");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>4</result>" +
                            "<comment>Invalid request format. Missed parameters: md5, id, sum, v1, date</comment>" +
                        "</response>"
                },
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "pay");
                            put("md5", "11111ff5a8661171111111d8e1171111");
                            put("id", String.valueOf(PAY_ID_SUCCESS));
                            put("v1", "demo");
                            put("sum", "100.20");
                            put("date", "2014-02-19 20:07:08");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>3</result>" +
                            "<comment>Invalid md5 signature</comment>" +
                        "</response>"
                },
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "pay");
                            put("md5", "151783427f854e7c61e54fae5361cacb");
                            put("id", String.valueOf(PAY_ID_SUCCESS));
                            put("v1", "demo");
                            put("sum", "100.20");
                            put("date", "DATE");
                        }},
                        "<response>" +
                            "<result>4</result>" +
                            "<comment>Date string 'DATE' does not match format 'Y-M-d H:m:s'</comment>" +
                        "</response>"
                },
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "pay");
                            put("md5", "b03894e48c0dfa6a9adda90739ca986c");
                            put("id", String.valueOf(PAY_ID_ANY_EXCEPTION));
                            put("v1", "demo");
                            put("sum", "100.20");
                            put("date", "2014-02-19 20:07:08");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>1</result>" +
                            "<comment>Any exception</comment>" +
                        "</response>"
                },
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "pay");
                            put("md5", "151783427f854e7c61e54fae5361cacb");
                            put("id", String.valueOf(PAY_ID_SUCCESS));
                            put("v1", "demo");
                            put("sum", "100.20");
                            put("date", "2014-02-19 20:07:08");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>0</result>" +
                            "<comment/>" +
                            "<id_shop>123100</id_shop>" +
                        "</response>"
                },
        };
    }

}
