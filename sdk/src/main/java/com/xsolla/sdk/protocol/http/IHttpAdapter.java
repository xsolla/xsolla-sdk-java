package com.xsolla.sdk.protocol.http;

import com.xsolla.sdk.protocol.request.Request;
import com.xsolla.sdk.protocol.response.Response;

public interface IHttpAdapter {

    public Request getRequest();

    public void sendResponse(Response response);
}
