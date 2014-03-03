package com.xsolla.sdk.protocol.http;


import com.xsolla.sdk.protocol.request.Request;
import com.xsolla.sdk.protocol.response.Response;

import java.util.LinkedHashMap;

/**
 * Для тестирования
 */
public class SimpleHttpAdapter implements IHttpAdapter {
    public Request getRequest() {
        return new Request() {{
            this.params = new LinkedHashMap<String, String>() {{
                put("command", "pay");
                put("id", "101");
                put("md5", "cfaab9a966ce29fcf9718cb027b95abd");
                put("sum", "100.20");
                put("v1", "demo");
                put("date", "2013-01-01 01:01:01");
            }};
        }};
    }

    public void sendResponse(Response response) {
        System.out.println("Sending response:");
        System.out.println(response.getContent());
        System.out.println("End");
    }
}
