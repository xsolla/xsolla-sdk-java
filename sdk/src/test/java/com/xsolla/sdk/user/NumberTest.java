/*
package com.xsolla.sdk.user;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.FluentStringsMap;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import com.xsolla.sdk.Project;
import com.xsolla.sdk.Property;
import com.xsolla.sdk.User;
import com.xsolla.sdk.exception.InternalServerException;
import com.xsolla.sdk.exception.InvalidArgumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NumberTest {
    protected Project projectMock;
    protected User userMock;
    protected AsyncHttpClient clientMock;
    protected AsyncHttpClient.BoundRequestBuilder requestBuilderMock;
    protected ListenableFuture<Response> futureResponseMock;
    protected Response responseMock;
    protected Number number;
    protected FluentStringsMap queryParameters;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception{
        this.projectMock = mock(Project.class);
        when(this.projectMock.getProjectId()).thenReturn(777);
        this.userMock = mock(User.class);
        when(this.userMock.getV1()).thenReturn("v1");
        when(this.userMock.getV2()).thenReturn("v2");
        when(this.userMock.getV3()).thenReturn("v3");
        when(this.userMock.getEmail()).thenReturn("email");
        this.requestBuilderMock = mock(AsyncHttpClient.BoundRequestBuilder.class);
        this.queryParameters = new FluentStringsMap();
        this.queryParameters.add("project", "777");
        this.queryParameters.add("v1", "v1");
        this.queryParameters.add("v2", "v2");
        this.queryParameters.add("v3", "v3");
        this.queryParameters.add("email", "email");
        this.queryParameters.add("format", "json");
        when(this.requestBuilderMock.setQueryParameters(this.queryParameters)).thenReturn(null);
        this.responseMock = mock(Response.class);
        this.futureResponseMock = (ListenableFuture<Response>)mock(ListenableFuture.class);
        when(this.futureResponseMock.get()).thenReturn(responseMock);
        when(this.requestBuilderMock.execute()).thenReturn(this.futureResponseMock);
        this.clientMock = mock(AsyncHttpClient.class);
        when(this.clientMock.prepareGet(Property.apiUrl + "/xsolla_number.php")).thenReturn(requestBuilderMock);
        this.number = new Number(this.clientMock, this.projectMock);
    }

    @After
    public void tearDown() throws Exception {
        verify(this.projectMock).getProjectId();
        verify(this.userMock).getV1();
        verify(this.userMock).getV2();
        verify(this.userMock).getV3();
        verify(this.userMock).getEmail();
        verify(this.clientMock).prepareGet(Property.apiUrl + "/xsolla_number.php");
        verify(this.requestBuilderMock).setQueryParameters(this.queryParameters);
        verify(this.requestBuilderMock).execute();
        verify(this.responseMock).getResponseBody();
    }

    @Test
    public void testGetNumber() throws Exception {
        when(this.responseMock.getResponseBody()).thenReturn("{\"result\":\"0\",\"number\":\"number\"}");
        assertEquals("number", this.number.getNumber(this.userMock));
    }

    @Test
    public void testGetNumberInternalException() throws Exception {
        thrown.expect(InternalServerException.class);
        thrown.expect(com.xsolla.sdk.CustomMatcher.hasCode(10));
        thrown.expectMessage("description");
        when(this.responseMock.getResponseBody()).thenReturn("{\"result\":\"10\",\"description\":\"description\"}");
        this.number.getNumber(this.userMock);
    }

    @Test
    public void testGetNumberInvalidArgumentException() throws Exception {
        thrown.expect(InvalidArgumentException.class);
        thrown.expect(com.xsolla.sdk.CustomMatcher.hasCode(1));
        thrown.expectMessage("description");
        when(this.responseMock.getResponseBody()).thenReturn("{\"result\":\"1\",\"description\":\"description\"}");
        this.number.getNumber(this.userMock);
    }
}
*/