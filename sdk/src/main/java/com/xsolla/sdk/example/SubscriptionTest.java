package com.xsolla.sdk.example;

import com.ning.http.client.AsyncHttpClient;
import com.xsolla.sdk.Invoice;
import com.xsolla.sdk.Project;
import com.xsolla.sdk.Subscription;
import com.xsolla.sdk.User;
import com.xsolla.sdk.user.Subscriptions;

import java.util.List;

public class SubscriptionTest {
    static public void main(String[] args) throws Exception {

        User user = new User("v1").setV2("v2");
        Project demoProject = new Project(
            4783,//demo project id
            "key"//demo project secret key
        );

        /**
         * Не компилиреутся, т.к. отличается сигнатура конструктора Subscriptions
         */
        /*
        Subscriptions subscription = new Subscriptions(
                new AsyncHttpClient(),
                "https://api.xsolla.com",
                demoProject
        );

        List<Subscription> userSubscriptions = subscription.search(user, Subscriptions.TYPE_CARD);

        Invoice invoice = new Invoice(100, null, null, null);
        Subscription userSubscription = userSubscriptions.iterator().next();
        subscription.pay(userSubscription, invoice);

        subscription.delete(userSubscription);
        */

    }
}

