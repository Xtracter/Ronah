package com.crazedout.ronah.service.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MultipartPart {
    public Map<String, String> headers;
    public Map<String, String> fields;
    public byte[] body;

    public MultipartPart(Map<String, String> headers, byte[] body) {
        this.headers = headers;
        this.body = body;
        this.fields = new HashMap<>();
        String cp = headers.get("content-disposition");
        String[] tokens = cp.split(";");
        for(String t:tokens){
            if(t.contains("=")){
                String[] pair = t.split("=");
                fields.put(pair[0].trim(),pair[1].replace("\"","").trim());
            }
        }
    }

    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }


    public String getField(String field){
        return this.fields.get(field);
    }

}
