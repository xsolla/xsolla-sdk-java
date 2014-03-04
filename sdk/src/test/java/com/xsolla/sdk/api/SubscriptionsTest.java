/*
package com.xsolla.sdk.user;

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
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class SubscriptionsTest {
    protected Subscriptions subscriptions;
    protected AsyncHttpClient clientMock;
    protected Project projectMock;
    protected AsyncHttpClient.BoundRequestBuilder requestBuilderMock;
    protected User userMock;
    protected ListenableFuture<Response> futureResponseMock;
    protected Response responseMock;
    protected Subscription subscriptionMock;
    protected Invoice invoiceMock;
    protected FluentStringsMap queryParameters;
    protected static final int subscriptionMockId = 222;
    protected static final int subscriptionMockType = 333;
    protected static final BigDecimal invoiceMockAmount = new BigDecimal(444);
    protected static final int invoiceMockId = 555;
    protected static final int projectMockId = 777;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception
    {
        this.projectMock = mock(Project.class);
        when(this.projectMock.getProjectId()).thenReturn(projectMockId);
        this.userMock = mock(User.class);
        when(this.userMock.getV1()).thenReturn("v1");
        when(this.userMock.getV2()).thenReturn("v2");
        when(this.userMock.getV3()).thenReturn("v3");
        this.requestBuilderMock = mock(AsyncHttpClient.BoundRequestBuilder.class);
        this.responseMock = mock(Response.class);
        this.futureResponseMock = (ListenableFuture<Response>)mock(ListenableFuture.class);
        when(this.futureResponseMock.get()).thenReturn(this.responseMock);
        when(this.requestBuilderMock.execute()).thenReturn(this.futureResponseMock);
        this.clientMock = mock(AsyncHttpClient.class);
        this.subscriptionMock = mock(Subscription.class);
        when(this.subscriptionMock.getId()).thenReturn(subscriptionMockId);
        when(this.subscriptionMock.getType()).thenReturn(subscriptionMockType);
        this.invoiceMock = mock(Invoice.class);
        when(this.invoiceMock.getAmount()).thenReturn(invoiceMockAmount);
        this.subscriptions = new Subscriptions(this.clientMock, this.projectMock);
    }

    @After
    public void tearDown() throws Exception {
        verify(this.projectMock).getProjectId();
        verify(this.requestBuilderMock).setQueryParameters(this.queryParameters);
        verify(this.requestBuilderMock).execute();
    }

    @Test
    public void testSearch() throws Exception
    {
        initQueryForSearch();
        when(this.requestBuilderMock.setQueryParameters(this.queryParameters)).thenReturn(null);
        when(this.clientMock.prepareGet(Subscriptions.URL)).thenReturn(requestBuilderMock);
        when(this.responseMock.getResponseBody()).thenReturn("{\"subscriptions\":[{\"id\":\"id\",\"name\":\"name\",\"type\":\"type\",\"currency\":\"currency\"}],\"number\":\"number\"}");
        this.subscriptions.search(this.userMock, Subscriptions.TYPE_CARD);
        verify(this.clientMock).prepareGet(Subscriptions.URL);
        verify(this.userMock).getV1();
        verify(this.userMock).getV2();
        verify(this.userMock).getV3();
        verify(this.subscriptionMock).getId();
        verify(this.subscriptionMock).getType();
        verify(this.invoiceMock).getAmount();
        verify(this.responseMock).getResponseBody();
    }

    @Test
    public void testSearchSecurityException() throws Exception
    {
        thrown.expect(com.xsolla.sdk.exception.SecurityException.class);
        when(this.responseMock.getStatusCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);
        when(this.responseMock.getResponseBody()).thenReturn("{\"error\":{\"code\":\"" + Subscriptions.ERROR_CODE_INVALID_SIGN + "\",\"message\":\"message\"}}");
        when(this.clientMock.prepareGet(Subscriptions.URL)).thenReturn(requestBuilderMock);
        initQueryForSearch();
        this.subscriptions.search(this.userMock, Subscriptions.TYPE_CARD);
        verify(this.clientMock).prepareGet(Subscriptions.URL);
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
        when(this.responseMock.getResponseBody()).thenReturn("{\"error\":{\"code\":\""+(Subscriptions.ERROR_CODE_INVALID_SIGN+1)+"\",\"message\":\"message\"}}");
        when(this.clientMock.prepareGet(Subscriptions.URL)).thenReturn(requestBuilderMock);
        initQueryForSearch();
        this.subscriptions.search(this.userMock, Subscriptions.TYPE_CARD);
        verify(this.clientMock).prepareGet(Subscriptions.URL);
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
        this.queryParameters.add("merchant_id", String.valueOf(projectMockId));
        this.queryParameters.add("v1", "v1");
        this.queryParameters.add("v2", "v2");
        this.queryParameters.add("v3", "v3");
        this.queryParameters.add("type", Subscriptions.TYPE_CARD);
        this.queryParameters.add("test", String.valueOf(false));
    }

    @Test
    public void testPay() throws Exception
    {
        String url = Subscriptions.URL + "/" + subscriptionMockType;
        when(this.clientMock.preparePost(url)).thenReturn(requestBuilderMock);
        when(this.responseMock.getResponseBody()).thenReturn("{\"id\":\"" + invoiceMockId + "\"}");
        initQueryForPay();
        assertEquals(invoiceMockId, this.subscriptions.pay(this.subscriptionMock, this.invoiceMock));
        verify(this.clientMock).preparePost(Subscriptions.URL + "/" + subscriptionMockType);
        verify(this.subscriptionMock).getId();
        verify(this.subscriptionMock).getType();
        verify(this.invoiceMock).getAmount();
        verify(this.responseMock).getResponseBody();
    }

    @Test
    public void testPayException() throws Exception
    {
        thrown.expect(InvalidArgumentException.class);
        String url = Subscriptions.URL + "/" + subscriptionMockType;
        when(this.clientMock.preparePost(url)).thenReturn(requestBuilderMock);
        when(this.responseMock.getStatusCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
        when(this.responseMock.getResponseBody()).thenReturn("{\"error\":{\"code\":\""+(Subscriptions.ERROR_CODE_INVALID_SIGN+1)+"\",\"message\":\"message\"}}");
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
        this.queryParameters.add("subscription_id", String.valueOf(subscriptionMockId));
        this.queryParameters.add("merchant_id", String.valueOf(projectMockId));
        this.queryParameters.add("amount_virtual", String.valueOf(invoiceMockAmount));
        this.queryParameters.add("card_cvv", null);
    }

    @Test
    public void testDelete() throws Exception
    {
        String url = Subscriptions.URL + "/" + subscriptionMockType;
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
        String url = Subscriptions.URL + "/" + subscriptionMockType;
        when(this.clientMock.prepareDelete(url)).thenReturn(requestBuilderMock);
        when(this.responseMock.getStatusCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
        when(this.responseMock.getResponseBody()).thenReturn("{\"error\":{\"code\":\""+(Subscriptions.ERROR_CODE_INVALID_SIGN+1)+"\",\"message\":\"message\"}}");
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
        queryParameters.add("merchant_id", String.valueOf(projectMockId));
        queryParameters.add("subscription_id", String.valueOf(subscriptionMockId));
    }
}
*/
