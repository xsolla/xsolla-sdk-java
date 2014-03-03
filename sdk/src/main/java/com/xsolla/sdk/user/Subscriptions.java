package com.xsolla.sdk.user;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.FluentStringsMap;
import com.ning.http.client.Response;
import com.xsolla.sdk.*;
import com.xsolla.sdk.exception.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.lang.Exception;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.Future;

public class Subscriptions {
    public static final String TYPE_CARD = "card";
    public static final String TYPE_PAYPAL = "paypal";
    public static final String TYPE_YANDEX = "yandex";
    public static final String TYPE_WEBMONEY = "wm";

    public static final String URL = Property.apiUrl + "/v1/subscriptions";

    public static final int ERROR_CODE_INVALID_SIGN = 23;

    protected Project project;
    protected AsyncHttpClient client;
    protected boolean isTest;

    public Subscriptions(AsyncHttpClient client, Project project)
    {
        this(client, project, false);
    }

    public Subscriptions(AsyncHttpClient client, Project project, boolean isTest)
    {
        this.client = client;
        this.project = project;
        this.isTest = isTest;
    }

    public List<Subscription> search(User user) throws Exception
    {
        return search(user, null);
    }

    /**
     *
     * @param user
     * @param type One of Subscriptions::TYPE_* constants
     * @return
     */
    public List<Subscription> search(User user, String type) throws Exception
    {
        AsyncHttpClient.BoundRequestBuilder requestBuilder = this.client.prepareGet(URL);
        FluentStringsMap queryParameters = new FluentStringsMap();
        queryParameters.add("merchant_id", String.valueOf(this.project.getProjectId()));
        queryParameters.add("v1", user.getV1());
        queryParameters.add("v2", user.getV2());
        queryParameters.add("v3", user.getV3());
        queryParameters.add("type", type);
        queryParameters.add("test", String.valueOf(this.isTest));
        requestBuilder.setQueryParameters(queryParameters);
        requestBuilder.setHeader("X-Xsolla-Sign", this.generateSign(queryParameters));
        Future<Response> futureResponse = requestBuilder.execute();
        Response response = futureResponse.get();
        this.checkResponseStatus(response);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject)parser.parse(response.getResponseBody());

        List<Subscription> subscriptions = new ArrayList<Subscription>();
        JSONArray subscriptionsRows = (JSONArray)jsonObject.get("subscriptions");
        Iterator<JSONObject> iterator = subscriptionsRows.iterator();
        while (iterator.hasNext()) {
            JSONObject subscription = iterator.next();
            subscriptions.add(
                    new Subscription(
                        Integer.parseInt(subscription.get("id").toString()),
                        Integer.parseInt(subscription.get("name").toString()),
                        Integer.parseInt(subscription.get("type").toString()),
                        Integer.parseInt(subscription.get("currency").toString())
                    )
            );
        }
        return subscriptions;
    }

    public int pay(Subscription subscription, Invoice invoice) throws Exception
    {
        return pay(subscription, invoice, null);
    }

    public int pay(Subscription subscription, Invoice invoice, String cardCvv) throws Exception
    {
        AsyncHttpClient.BoundRequestBuilder requestBuilder = this.client.preparePost(URL + '/' + subscription.getType());
        FluentStringsMap queryParameters = new FluentStringsMap();
        queryParameters.add("subscription_id", String.valueOf(subscription.getId()));
        queryParameters.add("merchant_id", String.valueOf(this.project.getProjectId()));
        queryParameters.add("amount_virtual", invoice.getAmount().toString());
        queryParameters.add("card_cvv", cardCvv);
        requestBuilder.setQueryParameters(queryParameters);
        requestBuilder.setHeader("X-Xsolla-Sign", this.generateSign(queryParameters));
        Future<Response> futureResponse = requestBuilder.execute();
        Response response = futureResponse.get();
        this.checkResponseStatus(response);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject)parser.parse(response.getResponseBody());
        return Integer.parseInt(jsonObject.get("id").toString());
    }

    public boolean delete(Subscription subscription) throws Exception
    {
        AsyncHttpClient.BoundRequestBuilder requestBuilder = this.client.prepareDelete(URL + '/' + subscription.getType());
        FluentStringsMap queryParameters = new FluentStringsMap();
        queryParameters.add("merchant_id", String.valueOf(this.project.getProjectId()));
        queryParameters.add("subscription_id", String.valueOf(subscription.getId()));
        requestBuilder.setQueryParameters(queryParameters);
        requestBuilder.setHeader("X-Xsolla-Sign", this.generateSign(queryParameters));
        Future<Response> futureResponse = requestBuilder.execute();
        Response response = futureResponse.get();
        this.checkResponseStatus(response);
        return HttpURLConnection.HTTP_NO_CONTENT == response.getStatusCode();
    }

    protected String generateSign(FluentStringsMap parameters) throws Exception
    {
        String signString = "";

        SortedSet<String> keys = new TreeSet<String>(parameters.keySet());
        for (String key : keys) {
            Collection<String> val = parameters.get(key);
            signString += key + "=" + val.iterator().next();
        }
        return new String(MessageDigest.getInstance("MD5").digest((signString + this.project.getSecretKey()).getBytes()));
    }

    public boolean checkResponseStatus(Response response) throws Exception
    {
        if (Arrays.asList(
                HttpURLConnection.HTTP_OK,
                HttpURLConnection.HTTP_ACCEPTED,
                HttpURLConnection.HTTP_CREATED,
                HttpURLConnection.HTTP_NO_CONTENT
        ).contains(response.getStatusCode()))
            return true;
        //error message
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject)parser.parse(response.getResponseBody());
        jsonObject = (JSONObject)jsonObject.get("error");
        String message = jsonObject.get("message").toString();
        int code = Integer.parseInt(jsonObject.get("code").toString());
        if (code == ERROR_CODE_INVALID_SIGN) {
            //throw new com.xsolla.sdk.exception.SecurityException(message, code);
            throw new com.xsolla.sdk.exception.SecurityException(message);
        }
        //throw new InvalidArgumentException(message, code);
        throw new InvalidArgumentException(message);
    }
}
