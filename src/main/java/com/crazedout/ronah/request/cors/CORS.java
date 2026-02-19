package com.crazedout.ronah.request.cors;

import com.crazedout.ronah.request.Request;

import java.util.List;

public class CORS {

    private static CORS instance;

    public static String getCORSHeaders(Request request, List<String> allow){
        String origin = request.getHeader("Origin");
        if(origin!=null){
            for(String s:allow){
                if(s.equalsIgnoreCase(origin))
                    return "Access-Control-Allow-Origin: " + origin + "\n" +
                            "Access-Control-Allow-Methods: GET, POST\n" +
                            "Access-Control-Allow-Headers: Content-Type, Authorization\n" +
                            "Access-Control-Allow-Credentials: true\n";
            }
        }
        return "";
    }

}
