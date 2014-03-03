package com.xsolla.sdk;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

public class QueryParser {

    public static HashMap<String, String> parseQuery(String query) throws UnsupportedEncodingException {
        String paramStrings[] = query.split("\\&");
        HashMap<String, String> params = new HashMap<String, String>();
        for (int i=0;i < paramStrings.length;i++) {
            String parts[] = paramStrings[i].split("=");
            params.put(URLDecoder.decode(parts[0], "UTF-8"), URLDecoder.decode(parts[1], "UTF-8"));
        }
        return params;
    }
}
