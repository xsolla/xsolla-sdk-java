package com.xsolla.samples.paymentpage;

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
