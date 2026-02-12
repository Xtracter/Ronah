package com.crazedout.ronah.service.handler;

import java.util.Map;

public class MultipartPart {
    public Map<String, String> headers;
    public byte[] body;

    public MultipartPart(Map<String, String> headers, byte[] body) {
        this.headers = headers;
        this.body = body;
    }

    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }
}
