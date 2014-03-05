package com.xsolla.samples.api;

import com.xsolla.sdk.Invoice;
import com.xsolla.sdk.Project;
import com.xsolla.sdk.Subscription;
import com.xsolla.sdk.User;
import com.xsolla.sdk.api.ApiFactory;
import com.xsolla.sdk.api.SubscriptionsApi;
import java.math.BigDecimal;
import java.util.List;

public class SubscriptionsApiSample {

    public static void main(String[] args) {
        Project project = new Project(
                4783, // demo project id
                "key" // demo project secret key
        );
        User user = new User("v1").setV2("v2");
        Invoice invoice = new Invoice().setVirtualCurrencyAmount(new BigDecimal(100));

        ApiFactory apiFactory = new ApiFactory(project);
        SubscriptionsApi subscriptionsApi = apiFactory.getSubscriptionsApi();

        try {
            List<Subscription> subscriptionList = subscriptionsApi.search(user, SubscriptionsApi.TYPE_CARD);
            if (subscriptionList.isEmpty()) {
                System.out.println("Subscriptions not found");
                return;
            }

            subscriptionsApi.pay(subscriptionList.get(0), invoice);
            subscriptionsApi.pay(subscriptionList.get(0), invoice);
            subscriptionsApi.delete(subscriptionList.get(0));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
