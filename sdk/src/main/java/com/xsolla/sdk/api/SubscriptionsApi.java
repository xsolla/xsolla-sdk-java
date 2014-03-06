package com.xsolla.sdk.api;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.FluentStringsMap;
import com.ning.http.client.Response;
import com.xsolla.sdk.*;
import com.xsolla.sdk.exception.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.lang.Exception;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SubscriptionsApi {
    public static final String TYPE_CARD = "card";
    public static final String TYPE_PAYPAL = "paypal";
    public static final String TYPE_YANDEX = "yandex";
    public static final String TYPE_WEBMONEY = "wm";

    public static final String API_URL = "/v1/subscriptions";

    public static final int ERROR_CODE_INVALID_SIGN = 23;

    protected Project project;
    protected String url;
    protected AsyncHttpClient asyncHttpClient;
    protected boolean isTest;

    public SubscriptionsApi(AsyncHttpClient asyncHttpClient, String baseUrl, Project project)
    {
        this(asyncHttpClient, baseUrl, project, false);
    }

    public SubscriptionsApi(AsyncHttpClient asyncHttpClient, String baseUrl, Project project, boolean isTest)
    {
        this.asyncHttpClient = asyncHttpClient;
        this.url = baseUrl + API_URL;
        this.project = project;
        this.isTest = isTest;
    }

    /**
     * Search subscription by User
     *
     * @param user User
     * @return List<Subscription>
     * @throws Exception
     */
    public List<Subscription> search(User user) throws Exception
    {
        return search(user, null);
    }

    /**
     * Search subscription by User and type
     * @param user
     * @param type May be: "card", "paypal", "yandex", "wm"
     * @return List<Subscription>
     * @throws Exception
     */
    public List<Subscription> search(User user, String type) throws Exception {
        AsyncHttpClient.BoundRequestBuilder requestBuilder = this.asyncHttpClient.prepareGet(this.url);
        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("merchant_id", String.valueOf(this.project.getProjectId()));
        queryParameters.put("v1", user.getV1());
        queryParameters.put("v2", user.getV2());
        queryParameters.put("v3", user.getV3());
        queryParameters.put("type", type);
        queryParameters.put("test", this.isTest ? "1" : "");
        this.setQueryParameters(requestBuilder, queryParameters);
        this.addSign(requestBuilder, queryParameters);
        Future<Response> futureResponse = requestBuilder.execute();
        Response response = futureResponse.get();
        this.checkResponseStatus(response);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(response.getResponseBody());

        List<Subscription> subscriptions = new ArrayList<Subscription>();
        JSONArray subscriptionsRows = (JSONArray) jsonObject.get("subscriptions");
        Iterator<JSONObject> iterator = subscriptionsRows.iterator();
        while (iterator.hasNext()) {
            JSONObject subscription = iterator.next();
            subscriptions.add(
                    new Subscription(
                        Integer.parseInt(subscription.get("id").toString()),
                        subscription.get("name").toString(),
                        subscription.get("type").toString(),
                        subscription.get("currency").toString()
                    )
            );
        }
        return subscriptions;
    }

    /**
     * Pay
     * @param subscription Subscription
     * @param invoice Invoice
     * @return Long
     * @throws Exception
     */
    public Long pay(Subscription subscription, Invoice invoice) throws Exception
    {
        return pay(subscription, invoice, null);
    }

    public Long pay(Subscription subscription, Invoice invoice, String cardCvv)
            throws NoSuchAlgorithmException, IOException, ExecutionException, InterruptedException, ParseException {
        AsyncHttpClient.BoundRequestBuilder requestBuilder = this.asyncHttpClient.preparePost(url + '/' + subscription.getType());
        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("subscription_id", subscription.getId());
        queryParameters.put("merchant_id", this.project.getProjectId());
        queryParameters.put("amount_virtual", invoice.getVirtualCurrencyAmount().toString());
        queryParameters.put("card_cvv", cardCvv == null ? "" : cardCvv);
        this.setRequestBodyJson(requestBuilder, queryParameters);
        this.addSign(requestBuilder, queryParameters);
        Future<Response> futureResponse = requestBuilder.execute();
        Response response = futureResponse.get();
        this.checkResponseStatus(response);
        String responseBody = response.getResponseBody();
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(responseBody);
        try {
            return Long.parseLong(jsonObject.get("id").toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Delete subscription
     * @param subscription Subscription
     * @return boolean
     * @throws Exception
     */
    public boolean delete(Subscription subscription) throws Exception
    {
        AsyncHttpClient.BoundRequestBuilder requestBuilder = this.asyncHttpClient.prepareDelete(url + '/' + subscription.getType());
        Map<String, Object> queryParameters = new HashMap<>();
        queryParameters.put("merchant_id", this.project.getProjectId());
        queryParameters.put("subscription_id", subscription.getId());
        this.setRequestBodyJson(requestBuilder, queryParameters);
        this.addSign(requestBuilder, queryParameters);
        Future<Response> futureResponse = requestBuilder.execute();
        Response response = futureResponse.get();
        this.checkResponseStatus(response);
        return HttpURLConnection.HTTP_NO_CONTENT == response.getStatusCode();
    }

    protected void addSign(AsyncHttpClient.BoundRequestBuilder requestBuilder, Map<String, Object> queryParameters)
            throws NoSuchAlgorithmException {
        requestBuilder.setHeader("X-Xsolla-Sign", this.generateSign(queryParameters));
    }

    protected String generateSign(Map<String, Object> parameters) throws NoSuchAlgorithmException {
        StringBuilder signParameters = new StringBuilder();
        SortedSet<String> keys = new TreeSet<>(parameters.keySet());
        for (String key : keys) {
            signParameters.append(key)
                    .append('=')
                    .append(this.nullSafeObjectToString(parameters.get(key)));
        }
        signParameters.append(project.getSecretKey());
        byte[] sign = MessageDigest.getInstance("MD5")
                .digest(signParameters.toString().getBytes());
        return String.format("%032x",new BigInteger(1, sign));
    }

    protected void setQueryParameters(AsyncHttpClient.BoundRequestBuilder requestBuilder, Map<String, Object> parameters) {
        for (String key : parameters.keySet()) {
            requestBuilder.addQueryParameter(key, this.nullSafeObjectToString(parameters.get(key)));
        }
    }

    protected String nullSafeObjectToString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    protected void setRequestBodyJson(AsyncHttpClient.BoundRequestBuilder requestBuilder, Map<String, Object> parameters) {
        JSONObject requestJson = new JSONObject();
        requestJson.putAll(parameters);
        requestBuilder.setBody(requestJson.toJSONString());
    }

    protected boolean checkResponseStatus(Response response) throws IOException, ParseException {
        if (Arrays.asList(
                HttpURLConnection.HTTP_OK,
                HttpURLConnection.HTTP_ACCEPTED,
                HttpURLConnection.HTTP_CREATED,
                HttpURLConnection.HTTP_NO_CONTENT
        ).contains(response.getStatusCode())) {
            return true;
        }
        JSONParser parser = new JSONParser();
        String responseBody = response.getResponseBody();
        JSONObject jsonObject = (JSONObject) parser.parse(responseBody);
        jsonObject = (JSONObject) jsonObject.get("error");
        String message = jsonObject.get("message").toString();
        int code = Integer.parseInt(jsonObject.get("code").toString());
        if (code == ERROR_CODE_INVALID_SIGN) {
            throw new com.xsolla.sdk.exception.SecurityException(message);
        }
        throw new InvalidArgumentException(message);
    }
}
