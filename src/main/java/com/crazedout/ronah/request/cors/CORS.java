package com.crazedout.ronah.request.cors;

import com.crazedout.ronah.request.Request;

import java.util.List;

/**
 * Class to handle CORS headers.
 */
public class CORS {
    /**
     * Add CORS headers.
     * @param request request http request
     * @param allow List origins allowed.
     */
    public static void getCORSHeaders(Request request, String... allow) {
        String origin = request.getHeader("Origin");
        if (origin != null) {
            for (String s : allow) {
                if (s.equalsIgnoreCase(origin)) {
                    request.getResponse().addHeader("Access-Control-Allow-Origin", origin);
                    request.getResponse().addHeader("Access-Control-Allow-Methods", "GET, POST");
                    request.getResponse().addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
                    request.getResponse().addHeader("Access-Control-Allow-Credentials", "true");
                }
            }
        }
    }
}
