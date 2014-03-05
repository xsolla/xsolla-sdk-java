package com.xsolla.samples.api;

import com.xsolla.sdk.Invoice;
import com.xsolla.sdk.Project;
import com.xsolla.sdk.User;
import com.xsolla.sdk.api.ApiFactory;
import com.xsolla.sdk.api.MobilePaymentApi;
import java.math.BigDecimal;

public class MobilePaymentApiSample {

    public static void main(String[] args) {
        Project project = new Project(
                4783, // demo project id
                "key" // demo project secret key
        );
        User user = new User("v1").setV2("v2").setPhone("79120000000");

        ApiFactory apiFactory = new ApiFactory(project);
        MobilePaymentApi mobilePaymentApi = apiFactory.getMobilePaymentApi();
        try {
            Invoice invoice = mobilePaymentApi.calculateAmount(user, new BigDecimal(1000));
            System.out.println("Cost of 1000 units of virtual currency: " + invoice.getAmount().toString());

            invoice = mobilePaymentApi.calculateVirtualCurrencyAmount(user, new BigDecimal(100));
            System.out.println("Amount of virtual currency that can be bought for 100 rubles: " +
                    invoice.getVirtualCurrencyAmount().toString());

            Invoice requestInvoice = new Invoice().setVirtualCurrencyAmount(new BigDecimal(100));
            invoice = mobilePaymentApi.createInvoice(user, requestInvoice);
            System.out.println("Issue an invoice for a virtual currency for 100 rubles. Your invoice number: " +
                    String.valueOf(invoice.getId())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
