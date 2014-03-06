Xsolla SDK for Java
===============

[![Build Status](https://travis-ci.org/xsolla/xsolla-sdk-java.png?branch=master)](https://travis-ci.org/xsolla/xsolla-sdk-java)

An official SDK for interacting with [Xsolla HTTP API](http://xsolla.github.io/)

## Requirements

* JDK 1.7

## Installation

Add to your pom.xml:
```
<dependency>
    <groupId>com.xsolla</groupId>
    <artifactId>sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

### Generate URL to [Payment Page](http://xsolla.github.io/en/plugindemonstration.html)

```
import com.xsolla.sdk.Invoice;
import com.xsolla.sdk.Project;
import com.xsolla.sdk.User;
import com.xsolla.sdk.paymentpage.UrlBuilderFactory;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

public class PaymentPage {

    public static void main(String[] args){
        User user = new User("username")
                .setEmail("example@example.com")
                .setPhone("79000000000");
        Project project = new Project(
                4783, // demo project id
                "key" // demo project secret key
        );
        Invoice invoice = new Invoice()
                .setVirtualCurrencyAmount(new BigDecimal(10));

        UrlBuilderFactory urlBuilderFactory = new UrlBuilderFactory(project);
        try {
            String url = urlBuilderFactory.getCreditCards()
                    .setInvoice(invoice)
                    .setUser(user)
                    .setCountry("US")
                    .setLocale("fr")
                    .setParameter("description", "Purchase description")
                    .getUrl();
            System.out.println("URL to PayStation payment page: " + url);

        } catch (NoSuchAlgorithmException e) {
            System.out.print("The problem of creating a signature. No MD5 algorithm in your Java environment.");
        }
    }
}
```

### Receive [Instant Payment Notification](http://xsolla.github.io/en/currency.html)

For receiving IPN requests you should implement [storage](https://github.com/xsolla/xsolla-sdk-java/tree/master/sdk/src/main/java/com/xsolla/sdk/protocol/storage) interfaces.
Also you can setup sql tables [samples/ipn_standard/src/mysql](https://github.com/xsolla/xsolla-sdk-java/tree/master/samples/ipn_standard/src/mysql).

Sample Servlet IPN handler for [Standard Protocol](http://xsolla.github.io/en/currency.html):
```
package com.xsolla.samples.ipn.standard;

import com.xsolla.samples.ipn.standard.storage.JdbcPaymentStandardStorage;
import com.xsolla.samples.ipn.standard.storage.JdbcUserStorage;
import com.xsolla.sdk.Project;
import com.xsolla.sdk.protocol.ProtocolFactory;
import com.xsolla.sdk.protocol.StandardProtocol;
import com.xsolla.sdk.protocol.http.ServletHttpAdapter;
import com.xsolla.sdk.protocol.request.Request;
import com.xsolla.sdk.protocol.response.Response;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

public class StandardServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        super.doPost(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        final int PROJECT_ID = 4783;
        final String PROJECT_KEY = "key";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://DB_HOST/DB_NAME", "DB_USER", "DB_PASSWORD");

            ServletHttpAdapter servletHttpAdapter = new ServletHttpAdapter(request, response);

            Request xsollaRequest = servletHttpAdapter.getRequest();
            Project project = new Project(PROJECT_ID, PROJECT_KEY);

            JdbcUserStorage userStorage = new JdbcUserStorage(connection);
            JdbcPaymentStandardStorage paymentStorage = new JdbcPaymentStandardStorage(connection);

            ProtocolFactory protocolFactory = new ProtocolFactory(project);
            StandardProtocol standardProtocol = protocolFactory.getStandardProtocol(userStorage, paymentStorage);
            Response xsollaResponse = standardProtocol.run(xsollaRequest);

            servletHttpAdapter.sendResponse(xsollaResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

If your WEB server software does not support JavaEE Servlets, you should implement [IHttpAdapter](https://github.com/xsolla/xsolla-sdk-java/blob/master/sdk/src/main/java/com/xsolla/sdk/protocol/http/IHttpAdapter.java).

### Samples

All samples you can find in [samples](https://github.com/xsolla/xsolla-sdk-java/tree/master/samples) folder.

## Additional resources

* [Website](http://xsolla.com)
* [Documentation](http://xsolla.github.io)
* [Status](http://status.xsolla.com)
