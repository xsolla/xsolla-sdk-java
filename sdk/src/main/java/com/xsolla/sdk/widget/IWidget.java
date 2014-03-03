package com.xsolla.sdk.widget;

import com.xsolla.sdk.Invoice;
import com.xsolla.sdk.User;
import java.util.HashMap;


interface IWidget {
    public String getLink(User user, Invoice invoice, HashMap<String, String> params) throws Exception;
}
