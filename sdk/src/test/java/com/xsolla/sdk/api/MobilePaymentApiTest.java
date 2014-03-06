package com.xsolla.sdk.api;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import com.xsolla.sdk.Invoice;
import com.xsolla.sdk.Project;
import com.xsolla.sdk.User;
import com.xsolla.sdk.exception.*;
import com.xsolla.sdk.exception.SecurityException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MobilePaymentApiTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    static final String BASE_URL = "https://test.xsolla.com";
    static final String API_URL = "/mobile/payment/index.php";

    protected AsyncHttpClient asyncHttpClientMock;
    protected AsyncHttpClient.BoundRequestBuilder requestBuilderMock;
    protected ListenableFuture<Response> listenableFuture;
    protected Response response;

    protected User user;
    protected Project project;
    protected MobilePaymentApi mobilePaymentApi;
    protected Invoice requestInvoice;

    protected String xmlResponseCalculate =
            "<response>\n" +
            "   <sum>100</sum>\n" +
            "   <out>1000</out>\n" +
            "   <result>0</result>\n" +
            "   <comment>OK</comment>\n" +
            "</response>";

    protected String xmlResponseWrongMd5 =
            "<response>\n" +
            "   <result>3</result>\n" +
            "   <comment>OK</comment>\n" +
            "</response>";

    protected String xmlResponseTechnicalError =
            "<response>\n" +
            "   <result>1</result>\n" +
            "   <comment>OK</comment>\n" +
            "</response>";

    protected String xmlResponseWrongNumber =
            "<response>\n" +
            "   <result>2</result>\n" +
            "   <comment>OK</comment>\n" +
            "</response>";

    protected String xmlResponseCalculateInvalidRequest =
            "<response>\n" +
            "   <result>4</result>\n" +
            "   <comment>OK</comment>\n" +
            "</response>";

    protected String xmlResponseCalculateLimitExceeded =
            "<response>\n" +
            "   <result>7</result>\n" +
            "   <comment>OK</comment>\n" +
            "</response>";

    protected String xmlResponseCreateInvoice =
            "<response>\n" +
            "   <result>0</result>\n" +
            "   <comment>OK</comment>\n" +
            "   <invoice>1</invoice>\n" +
            "</response>";

    protected String xmlResponseErrorWithoutComment =
            "<response>\n" +
            "   <result>1</result>\n" +
            "</response>";

    @Before
    public void setUp() throws InterruptedException, ExecutionException, IOException {
        this.setUpAsyncHttpClient();
        this.user = new User("v1").setV2("v2").setPhone("79120000000").setEmail("email@example.com");
        this.project = new Project(4783, "key");
        this.requestInvoice = new Invoice().setAmount(new BigDecimal(100))
                .setVirtualCurrencyAmount(new BigDecimal(1000));
        mobilePaymentApi = new MobilePaymentApi(this.asyncHttpClientMock, BASE_URL, this.project);
    }

    protected void setUpAsyncHttpClient() throws ExecutionException, InterruptedException, IOException {
        this.asyncHttpClientMock = mock(AsyncHttpClient.class);
        this.requestBuilderMock = mock(AsyncHttpClient.BoundRequestBuilder.class);
        this.listenableFuture = (ListenableFuture<Response>) mock(ListenableFuture.class);
        this.response = mock(Response.class);
        when(this.listenableFuture.get()).thenReturn(this.response);
        when(this.requestBuilderMock.execute()).thenReturn(this.listenableFuture);
        when(this.asyncHttpClientMock.prepareGet(eq(BASE_URL + API_URL))).thenReturn(this.requestBuilderMock);
    }

    @Test
    public void testCalculateVirtualCurrencyAmount() throws Exception {
        when(this.response.getResponseBody()).thenReturn(this.xmlResponseCalculate);
        Invoice responseInvoice = this.mobilePaymentApi.calculateVirtualCurrencyAmount(this.user, new BigDecimal(100));
        BigDecimal expectedVirtualCurrencyAmount = new BigDecimal(1000);
        assertTrue(responseInvoice.getVirtualCurrencyAmount().compareTo(expectedVirtualCurrencyAmount) == 0);
    }

    @Test
    public void testCalculateAmount() throws Exception {
        when(this.response.getResponseBody()).thenReturn(this.xmlResponseCalculate);
        Invoice responseInvoice = this.mobilePaymentApi.calculateAmount(this.user, new BigDecimal(1000));
        BigDecimal expectedAmount = new BigDecimal(100);
        assertTrue(responseInvoice.getAmount().compareTo(expectedAmount) == 0);
    }

    @Test
    public void testCalculateWithWrongMd5() throws Exception {
        this.thrown.expect(SecurityException.class);
        when(this.response.getResponseBody()).thenReturn(this.xmlResponseWrongMd5);
        this.mobilePaymentApi.calculateAmount(this.user, new BigDecimal(1000));
    }

    @Test
    public void testCalculateWithTemporaryError() throws Exception {
        this.thrown.expect(InternalServerException.class);
        when(this.response.getResponseBody()).thenReturn(this.xmlResponseTechnicalError);
        this.mobilePaymentApi.calculateAmount(this.user, new BigDecimal(1000));
    }

    @Test
    public void testCalculateWithWrongNumber() throws Exception {
        this.thrown.expect(InvalidArgumentException.class);
        when(this.response.getResponseBody()).thenReturn(this.xmlResponseWrongNumber);
        this.mobilePaymentApi.calculateAmount(this.user, new BigDecimal(1000));
    }

    @Test
    public void testCalculateWithInvalidRequest() throws Exception {
        this.thrown.expect(InvalidArgumentException.class);
        when(this.response.getResponseBody()).thenReturn(this.xmlResponseCalculateInvalidRequest);
        this.mobilePaymentApi.calculateAmount(this.user, new BigDecimal(1000));
    }

    @Test
    public void testCalculateWithExceeded() throws Exception {
        this.thrown.expect(InvalidArgumentException.class);
        when(this.response.getResponseBody()).thenReturn(this.xmlResponseCalculateLimitExceeded);
        this.mobilePaymentApi.calculateAmount(this.user, new BigDecimal(1000));
    }

    @Test
    public void testCreateInvoice() throws Exception {
        when(this.response.getResponseBody()).thenReturn(this.xmlResponseCreateInvoice);
        Invoice responseInvoice = this.mobilePaymentApi.createInvoice(this.user, this.requestInvoice);
        assertEquals(new Long(1), responseInvoice.getId());
    }

    @Test
    public void testCreateInvoiceWithWrongMd5() throws Exception {
        this.thrown.expect(SecurityException.class);
        when(this.response.getResponseBody()).thenReturn(this.xmlResponseWrongMd5);
        this.mobilePaymentApi.createInvoice(this.user, this.requestInvoice);
    }

    @Test
    public void testCreateInvoiceWithInvalidRequest() throws Exception {
        this.thrown.expect(InvalidArgumentException.class);
        when(this.response.getResponseBody()).thenReturn(this.xmlResponseCalculateInvalidRequest);
        this.mobilePaymentApi.createInvoice(this.user, this.requestInvoice);
    }

    @Test
    public void testCreateInvoiceWithTechnicalError() throws Exception {
        this.thrown.expect(InvalidResponseException.class);
        when(this.response.getResponseBody()).thenReturn(this.xmlResponseErrorWithoutComment);
        this.mobilePaymentApi.createInvoice(this.user, this.requestInvoice);
    }



}
