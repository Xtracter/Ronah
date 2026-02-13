package com.crazedout.ronah;

import com.crazedout.ronah.annotation.GET;
import com.crazedout.ronah.service.AutoRegisterService;
import com.crazedout.ronah.service.Request;

public class TestService extends AutoRegisterService {

    @GET(path="/index", response = "text/text")
    public static void test1(Request request){
        request.getResponse().ok("OK").send();
    }

}
