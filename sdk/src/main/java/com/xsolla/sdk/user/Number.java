package com.xsolla.sdk.user;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.FluentStringsMap;
import com.xsolla.sdk.Property;
import com.xsolla.sdk.exception.InternalServerException;
import com.xsolla.sdk.exception.InvalidArgumentException;
import com.xsolla.sdk.Project;
import com.xsolla.sdk.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.ning.http.client.Response;
import java.util.Arrays;
import java.util.concurrent.Future;

public class Number {
    protected Project project;
    protected AsyncHttpClient client;

    public Number(AsyncHttpClient client, Project project)
    {
        this.client = client;
        this.project = project;
    }

    public String getNumber(User user) throws Exception
    {
        AsyncHttpClient.BoundRequestBuilder requestBuilder = this.client.prepareGet(Property.apiUrl + "/xsolla_number.php");
        FluentStringsMap queryParameters = new FluentStringsMap();
        queryParameters.add("project", String.valueOf(this.project.getProjectId()));
        queryParameters.add("v1", user.getV1());
        queryParameters.add("v2", user.getV2());
        queryParameters.add("v3", user.getV3());
        queryParameters.add("email", user.getEmail());
        queryParameters.add("format", "json");
        requestBuilder.setQueryParameters(queryParameters);
        Future<Response> futureResponse = requestBuilder.execute();
        Response response = futureResponse.get();
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject)parser.parse(response.getResponseBody());
        int result = Integer.parseInt(jsonObject.get("result").toString());
        if (result == 0) {
            return jsonObject.get("number").toString();
        }
        String description = jsonObject.get("description").toString();
        if (Arrays.asList(10, 11).contains(result)) {
            //throw new InternalServerException(description, result);
            throw new InternalServerException(description);
        }
        //throw new InvalidArgumentException(description, result);
        throw new InvalidArgumentException(description);
    }
}
