package com.xsolla.sdk.widget;

import com.ning.http.client.AsyncHttpClient;
import com.xsolla.sdk.Invoice;
import com.xsolla.sdk.Project;
import com.xsolla.sdk.User;
import com.xsolla.sdk.utils.MapUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

abstract class Widget implements IWidget {
    final String BASE_URL = "https://secure.xsolla.com/paystation2/?";

    protected Project project;

    Widget(Project project) {
        this.project = project;
    }

    public String getLink(User user, Invoice invoice) throws NoSuchAlgorithmException,
            IllegalArgumentException {
        return getLink(user, invoice, new HashMap<String, String>());
    }

    public String getLink(User user, Invoice invoice, HashMap<String, String> params) throws NoSuchAlgorithmException,
            IllegalArgumentException {
        HashMap<String, String> paramsMerged = MapUtils.mergeHashMapsWithReplace(getDefaultParams(), params);
        paramsMerged.put("marketplace", this.getMarketPlace());
        paramsMerged.put("project", Integer.toString(this.project.getProjectId()));
        paramsMerged.put("v1", user.getV1());
        paramsMerged.put("v2", user.getV2());
        paramsMerged.put("v3", user.getV3());
        paramsMerged.put("email", user.getEmail());
        paramsMerged.put("userip", user.getUserIP());
        paramsMerged.put("phone", user.getPhone());
        if (invoice != null) {
            if ((invoice.getAmount()) != null && (invoice.getAmount().compareTo(BigDecimal.ZERO) != 0)) {
                paramsMerged.put("out", invoice.getAmount().toString());
            }
            if (invoice.getCurrency() != null) {
                paramsMerged.put("currency", invoice.getCurrency());
            }
        }

        HashMap<String, String> paramsFiltered = new HashMap<String, String>();
        for (String param : paramsMerged.keySet()) {
            String value = paramsMerged.get(param);
            if (value != null && !value.isEmpty()) {
                paramsFiltered.put(param, value);
            }
        }
        paramsMerged = null;

        this.checkRequiredParams(paramsFiltered);
        paramsFiltered.put("sign", this.generateSign(paramsFiltered));
        StringBuilder linkStringBuilder = new StringBuilder(this.BASE_URL);
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        AsyncHttpClient.BoundRequestBuilder boundRequestBuilder = asyncHttpClient.prepareGet(this.BASE_URL);
        for (String param : paramsFiltered.keySet()) {
            boundRequestBuilder.addQueryParameter(param, paramsFiltered.get(param));
        }
        return boundRequestBuilder.build().getUrl();
    }

    private boolean checkRequiredParams(HashMap<String, String> params) throws IllegalArgumentException {
        List<String> requiredParams = this.getRequiredParams();
        for (String param : requiredParams) {
            if (!params.containsKey(param)) {
                throw new IllegalArgumentException("Parameter " + param + " is not defined");
            }
        }
        return true;
    }

    public String generateSign(HashMap<String, String> params) throws NoSuchAlgorithmException {
        List<String> keys = this.signParamList();
        Collections.sort(keys);
        StringBuilder paramsStringBuilder = new StringBuilder("");
        for (String key : keys) {
            if (params.containsKey(key)) {
                paramsStringBuilder.append(key).append('=').append(params.get(key));
            }
        }
        paramsStringBuilder.append(this.project.getSecretKey());
        String paramsString = paramsStringBuilder.toString();
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(paramsString.getBytes(), 0, paramsString.length());
        return new BigInteger(1, messageDigest.digest()).toString(16);
    }

    abstract protected String getMarketPlace();

    abstract protected List<String> getRequiredParams();

    protected HashMap<String, String> getDefaultParams() {
        return new HashMap<String, String>();
    }

    private List<String> signParamList() {
        String[] params = {"theme", "project", "signparams", "v0", "v1", "v2", "v3", "out", "email",
                "currency", "userip", "allowSubscription", "fastcheckout"};
        return Arrays.asList(params);
    }
}
