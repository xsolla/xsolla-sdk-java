package com.xsolla.sdk.api;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.FluentStringsMap;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import com.xsolla.sdk.*;
import com.xsolla.sdk.exception.InvalidArgumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.lang.Exception;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SubscriptionsApiTest {

    protected SubscriptionsApi subscriptions;

    protected static final String BASE_URL = "https://api.xsolla.com";
    protected static final String API_URL = "/v1/subscriptions";

    protected AsyncHttpClient clientMock;
    protected Project projectMock;
    protected AsyncHttpClient.BoundRequestBuilder requestBuilderMock;
    protected User userMock;
    protected ListenableFuture<Response> futureResponseMock;
    protected Response responseMock;
    protected Subscription subscriptionMock;
    protected Invoice invoiceMock;
    protected FluentStringsMap queryParameters;

    protected static final int subscriptionId = 222;
    protected static final String subscriptionType = "type";
    protected static final BigDecimal amount = new BigDecimal(100);
    protected static final BigDecimal virtualCurrencyAmount = new BigDecimal(1000);
    protected static final long invoiceId = 555;
    protected static final int projectId = 777;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception
    {
        this.projectMock = mock(Project.class);
        when(this.projectMock.getProjectId()).thenReturn(projectId);
        this.userMock = mock(User.class);
        when(this.userMock.getV1()).thenReturn("v1");
        when(this.userMock.getV2()).thenReturn("v2");
        when(this.userMock.getV3()).thenReturn("v3");
        this.requestBuilderMock = mock(AsyncHttpClient.BoundRequestBuilder.class);
        this.responseMock = mock(Response.class);
        this.futureResponseMock = (ListenableFuture<Response>) mock(ListenableFuture.class);
        when(this.futureResponseMock.get()).thenReturn(this.responseMock);
        when(this.requestBuilderMock.execute()).thenReturn(this.futureResponseMock);
        this.clientMock = mock(AsyncHttpClient.class);
        this.subscriptionMock = mock(Subscription.class);
        when(this.subscriptionMock.getId()).thenReturn(subscriptionId);
        when(this.subscriptionMock.getType()).thenReturn(subscriptionType);
        this.invoiceMock = mock(Invoice.class);
        when(this.invoiceMock.getAmount()).thenReturn(amount);
        when(this.invoiceMock.getVirtualCurrencyAmount()).thenReturn(virtualCurrencyAmount);
        this.subscriptions = new SubscriptionsApi(this.clientMock, BASE_URL, this.projectMock);
    }

    @After
    public void tearDown() throws Exception {
        verify(this.projectMock).getProjectId();
        // verify(this.requestBuilderMock).setQueryParameters(this.queryParameters);
        verify(this.requestBuilderMock).execute();
    }

    @Test
    public void testSearch() throws Exception
    {
        initQueryForSearch();
        when(this.requestBuilderMock.setQueryParameters(this.queryParameters)).thenReturn(null);
        when(this.clientMock.prepareGet(eq(BASE_URL + API_URL))).thenReturn(requestBuilderMock);
        when(this.responseMock.getStatusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(this.responseMock.getResponseBody()).thenReturn("{\"subscriptions\":[{\"id\":222,\"name\":\"name\",\"type\":\"type\",\"currency\":\"currency\"}]}");
        this.subscriptions.search(this.userMock, SubscriptionsApi.TYPE_CARD);
        verify(this.clientMock).prepareGet(BASE_URL + API_URL);
        verify(this.userMock).getV1();
        verify(this.userMock).getV2();
        verify(this.userMock).getV3();
        verify(this.responseMock).getResponseBody();
    }

    @Test
    public void testSearchSecurityException() throws Exception
    {
        thrown.expect(com.xsolla.sdk.exception.SecurityException.class);
        when(this.responseMock.getStatusCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);
        when(this.responseMock.getResponseBody()).thenReturn("{\"error\":{\"code\":\"" + SubscriptionsApi.ERROR_CODE_INVALID_SIGN + "\",\"message\":\"message\"}}");
        when(this.clientMock.prepareGet(BASE_URL + API_URL)).thenReturn(requestBuilderMock);
        initQueryForSearch();
        this.subscriptions.search(this.userMock, SubscriptionsApi.TYPE_CARD);
        verify(this.clientMock).prepareGet(BASE_URL + API_URL);
        verify(this.userMock).getV1();
        verify(this.userMock).getV2();
        verify(this.userMock).getV3();
        verify(this.responseMock).getResponseBody();
    }

    @Test
    public void testSearchInvalidArgumentException() throws Exception
    {
        thrown.expect(InvalidArgumentException.class);
        when(this.responseMock.getStatusCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
        when(this.responseMock.getResponseBody()).thenReturn("{\"error\":{\"code\":\""+(SubscriptionsApi.ERROR_CODE_INVALID_SIGN+1)+"\",\"message\":\"message\"}}");
        when(this.clientMock.prepareGet(BASE_URL + API_URL)).thenReturn(requestBuilderMock);
        initQueryForSearch();
        this.subscriptions.search(this.userMock, SubscriptionsApi.TYPE_CARD);
        verify(this.clientMock).prepareGet(BASE_URL + API_URL);
        verify(this.userMock).getV1();
        verify(this.userMock).getV2();
        verify(this.userMock).getV3();
        verify(this.subscriptionMock).getId();
        verify(this.subscriptionMock).getType();
        verify(this.invoiceMock).getAmount();
        verify(this.responseMock).getResponseBody();
    }
      
    private void initQueryForSearch()
    {
        this.queryParameters = new FluentStringsMap();
        this.queryParameters.add("merchant_id", String.valueOf(projectId));
        this.queryParameters.add("v1", "v1");
        this.queryParameters.add("v2", "v2");
        this.queryParameters.add("v3", "v3");
        this.queryParameters.add("type", SubscriptionsApi.TYPE_CARD);
        this.queryParameters.add("test", String.valueOf(false));
    }

    @Test
    public void testPay() throws Exception
    {
        String url = BASE_URL + API_URL + "/" + subscriptionType;
        when(this.clientMock.preparePost(url)).thenReturn(requestBuilderMock);
        when(this.responseMock.getStatusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(this.responseMock.getResponseBody()).thenReturn("{\"id\":\"" + invoiceId + "\"}");
        initQueryForPay();
        long id = this.subscriptions.pay(this.subscriptionMock, this.invoiceMock);
        assertEquals(invoiceId, id);
        verify(this.clientMock).preparePost(BASE_URL + API_URL + "/" + subscriptionType);
        verify(this.subscriptionMock).getId();
        verify(this.subscriptionMock).getType();
        verify(this.invoiceMock).getVirtualCurrencyAmount();
        verify(this.responseMock).getResponseBody();
    }

    @Test
    public void testPayException() throws Exception
    {
        thrown.expect(InvalidArgumentException.class);
        String url = BASE_URL + API_URL + "/" + subscriptionType;
        when(this.clientMock.preparePost(url)).thenReturn(requestBuilderMock);
        when(this.responseMock.getStatusCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
        when(this.responseMock.getResponseBody()).thenReturn("{\"error\":{\"code\":\""+(SubscriptionsApi.ERROR_CODE_INVALID_SIGN+1)+"\",\"message\":\"message\"}}");
        initQueryForPay();
        this.subscriptions.pay(this.subscriptionMock, this.invoiceMock);
        verify(this.clientMock).preparePost(url);
        verify(this.userMock).getV1();
        verify(this.userMock).getV2();
        verify(this.userMock).getV3();
        verify(this.subscriptionMock).getId();
        verify(this.subscriptionMock).getType();
        verify(this.invoiceMock).getAmount();
        verify(this.responseMock).getResponseBody();
    }

    private void initQueryForPay()
    {
        this.queryParameters = new FluentStringsMap();
        this.queryParameters.add("subscription_id", String.valueOf(subscriptionId));
        this.queryParameters.add("merchant_id", String.valueOf(projectId));
        this.queryParameters.add("amount_virtual", String.valueOf(amount));
        this.queryParameters.add("card_cvv", null);
    }

    @Test
    public void testDelete() throws Exception
    {
        String url = BASE_URL + API_URL + "/" + subscriptionType;
        when(this.clientMock.prepareDelete(url)).thenReturn(requestBuilderMock);
        when(this.responseMock.getStatusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        initQueryForDelete();
        this.subscriptions.delete(this.subscriptionMock);
        verify(this.clientMock).prepareDelete(url);
        verify(this.subscriptionMock).getId();
        verify(this.subscriptionMock).getType();
    }

    @Test
    public void testDeleteException() throws Exception
    {
        thrown.expect(InvalidArgumentException.class);
        String url = BASE_URL + API_URL + "/" + subscriptionType;
        when(this.clientMock.prepareDelete(url)).thenReturn(requestBuilderMock);
        when(this.responseMock.getStatusCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
        when(this.responseMock.getResponseBody()).thenReturn("{\"error\":{\"code\":\""+(SubscriptionsApi.ERROR_CODE_INVALID_SIGN+1)+"\",\"message\":\"message\"}}");
        initQueryForDelete();
        this.subscriptions.delete(this.subscriptionMock);
        verify(this.clientMock).prepareDelete(url);
        verify(this.userMock).getV1();
        verify(this.userMock).getV2();
        verify(this.userMock).getV3();
        verify(this.subscriptionMock).getId();
        verify(this.subscriptionMock).getType();
        verify(this.invoiceMock).getAmount();
        verify(this.responseMock).getResponseBody();
    }

    private void initQueryForDelete()
    {
        this.queryParameters = new FluentStringsMap();
        queryParameters.add("merchant_id", String.valueOf(projectId));
        queryParameters.add("subscription_id", String.valueOf(subscriptionId));
    }
}
