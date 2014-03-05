package com.xsolla.sdk.api;

import com.ning.http.client.AsyncHttpClient;
import com.xsolla.sdk.Project;

public class ApiFactory {

    public static final String BASE_URL = "https://api.xsolla.com";

    protected Project project;
    protected String baseUrl;
    protected AsyncHttpClient asyncHttpClient;

    public ApiFactory(Project project, String baseUrl) {
        this.project = project;
        this.baseUrl = baseUrl;
        this.asyncHttpClient = new AsyncHttpClient();

    }

    public ApiFactory(Project project) {
        this(project, BASE_URL);
    }

    public SubscriptionsApi getSubscriptionsApi() {
        return new SubscriptionsApi(this.asyncHttpClient, this.baseUrl, project);
    }
}
