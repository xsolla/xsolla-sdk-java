package com.xsolla.sdk.protocol;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.xsolla.sdk.Project;
import com.xsolla.sdk.exception.*;
import com.xsolla.sdk.exception.SecurityException;
import com.xsolla.sdk.protocol.request.Request;
import com.xsolla.sdk.protocol.response.Response;
import com.xsolla.sdk.protocol.storage.IPaymentStorage;
import com.xsolla.sdk.validator.IpChecker;
import org.junit.Before;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

abstract public class ProtocolFullTest {

    static final int PROJECT_ID = 12345;
    static final String PROJECT_KEY = "key";
    static final String CLIENT_IP = "127.0.0.1";

    static final long CANCEL_ID_VALID = 100;
    static final long CANCEL_ID_NOT_FOUND = 101;
    static final long CANCEL_ID_UNPROCESSABLE = 102;
    static final long CANCEL_ID_ANY_EXCEPTION = 103;

    protected Project projectMock;
    protected Request requestMock;
    protected IpChecker ipCheckerMock;

    protected ProtocolFactory protocolFactory;

    @Before
    public void setUp() throws Exception {
        this.projectMock = mock(Project.class);
        when(this.projectMock.getProjectId()).thenReturn(PROJECT_ID);
        when(this.projectMock.getSecretKey()).thenReturn(PROJECT_KEY);

        this.ipCheckerMock = mock(IpChecker.class);

        this.protocolFactory = new ProtocolFactory(this.projectMock, this.ipCheckerMock);

    }

    @DataProvider
    public static Object[][] cancelDataProvider() {
        return new Object[][] {
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "cancel");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>4</result>" +
                            "<comment>Invalid request format. Missed parameters: md5, id</comment>" +
                        "</response>"
                },
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "cancel");
                            put("id", "500");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>4</result>" +
                            "<comment>Invalid request format. Missed parameters: md5</comment>" +
                        "</response>"
                },
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "cancel");
                            put("id", "500");
                            put("md5", "11111109bbf31111211175a111c3f11");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>3</result>" +
                            "<comment>Invalid md5 signature</comment>" +
                        "</response>"
                },
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "cancel");
                            put("id", String.valueOf(CANCEL_ID_NOT_FOUND));
                            put("md5", "19715f09bc5b2e9c2e47ce00cb40fc54");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>2</result>" +
                            "<comment>Invoice not found</comment>" +
                        "</response>"
                },
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "cancel");
                            put("id", String.valueOf(CANCEL_ID_UNPROCESSABLE));
                            put("md5", "9e652f044a63f2248633eb9a8afecf8e");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>7</result>" +
                                "<comment>exception message</comment>" +
                        "</response>"
                },
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "cancel");
                            put("id", String.valueOf(CANCEL_ID_ANY_EXCEPTION));
                            put("md5", "a4bbcb6f3e1da6366661181f888ef8be");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<response>" +
                            "<result>1</result>" +
                            "<comment>Any exception</comment>" +
                        "</response>"
                },
                {
                        new LinkedHashMap<String, String>() {{
                            put("command", "cancel");
                            put("id", String.valueOf(CANCEL_ID_VALID));
                            put("md5", "bc763ff5a8665c7c4c5fa0d8eba75ac8");
                        }},
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                "<response>" +
                                "<result>0</result>" +
                                "<comment/>" +
                                "</response>"
                }

        };
    }

    protected Request buildRequestMock(HashMap<String, String> params) {
        Request requestMock = mock(Request.class);
        final HashMap<String, String> mockParams = params;
        when(requestMock.getParam(anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                String parameter = (String) invocationOnMock.getArguments()[0];
                return mockParams.get(parameter);
            }
        });
        when(requestMock.getParams()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return mockParams;
            }
        });
        when(requestMock.hasParam(anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                String parameter = (String) invocationOnMock.getArguments()[0];
                return mockParams.containsKey(parameter);
            }
        });
        when(requestMock.getClientIP()).thenReturn(CLIENT_IP);
        return requestMock;
    }

    protected void addCancelHandler(IPaymentStorage paymentStorageMock) throws Exception {
        doThrow(new Exception("Any exception"))
                .when(paymentStorageMock).cancel(eq(CANCEL_ID_ANY_EXCEPTION));
        doThrow(new UnprocessableRequestException("exception message"))
                .when(paymentStorageMock).cancel(eq(CANCEL_ID_UNPROCESSABLE));
        doThrow(new InvoiceNotFoundException("Invoice not found"))
                .when(paymentStorageMock).cancel(eq(CANCEL_ID_NOT_FOUND));
    }
}
