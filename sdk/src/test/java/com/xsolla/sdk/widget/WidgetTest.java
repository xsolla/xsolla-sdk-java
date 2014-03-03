package com.xsolla.sdk.widget;

import com.xsolla.sdk.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.lang.Exception;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WidgetTest {
    protected Project projectMock;
    protected User userMock;
    protected Invoice invoiceMock;
    protected Paystation paystation;
    protected static final int projectMockId = 777;
    protected static final String projectMockSecretKey = "mykey";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception
    {
        this.userMock = mock(User.class);
        when(this.userMock.getEmail()).thenReturn("email");
        when(this.userMock.getUserIP()).thenReturn("userIP");
        when(this.userMock.getV1()).thenReturn("v1");
        when(this.userMock.getPhone()).thenReturn("phone");
        this.invoiceMock = mock(Invoice.class);
        this.projectMock = mock(Project.class);
        when(this.projectMock.getProjectId()).thenReturn(projectMockId);
        when(this.projectMock.getSecretKey()).thenReturn(projectMockSecretKey);
    }

    @After
    public void tearDown() throws Exception {
        verify(this.projectMock).getProjectId();
        verify(this.userMock).getEmail();
        verify(this.userMock).getUserIP();
        verify(this.userMock).getV1();
        verify(this.userMock).getPhone();
    }

    @Test
    public void testDirectPaymentWithoutPid() throws Exception
    {
        thrown.expect(IllegalArgumentException.class);
        this.paystation = new Directpayment(this.projectMock);
        this.paystation.getLink(this.userMock, this.invoiceMock);
    }

    @Test
    public void testMobileWidgetWithoutPid() throws Exception
    {
        this.paystation = new MobilePayment(this.projectMock);
        URL url = new URL(this.paystation.getLink(this.userMock, this.invoiceMock));
        String query = url.getQuery();
        assertEquals("1738", QueryParser.parseQuery(query).get("pid"));
    }

    @Test
    public void testCreditCardsWithoutPid() throws Exception
    {
        this.paystation = new CreditCards(this.projectMock);
        URL url = new URL(this.paystation.getLink(this.userMock, this.invoiceMock));
        String query = url.getQuery();
        assertEquals("1380", QueryParser.parseQuery(query).get("pid"));
    }

    @Test
    public void testPaystationWithNullParameters() throws Exception
    {
        when(this.userMock.getV2()).thenReturn("");
        this.paystation = new Paystation(this.projectMock);
        URL url = new URL(this.paystation.getLink(this.userMock, this.invoiceMock));
        String query = url.getQuery();
        assertEquals(null, QueryParser.parseQuery(query).get("v2"));
        verify(this.userMock).getV2();
    }

    @Test
    public void testPaystationWithTrueParameters() throws NoSuchAlgorithmException
    {
        this.paystation = new Paystation(this.projectMock);
        String url = "https://secure.xsolla.com/paystation2/?sign=6bb83e082abf5dadfb3663747d87d79a&v1=v1&project="+projectMockId+"&phone=phone&email=email&marketplace=paystation&userip=userIP";
        assertEquals(url, this.paystation.getLink(this.userMock, this.invoiceMock));
    }
}
