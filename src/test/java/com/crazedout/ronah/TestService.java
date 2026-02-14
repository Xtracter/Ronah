package com.crazedout.ronah;

import com.crazedout.ronah.annotation.GET;
import com.crazedout.ronah.annotation.POST;
import com.crazedout.ronah.annotation.Param;
import com.crazedout.ronah.service.AutoRegisterService;
import com.crazedout.ronah.service.Request;
import org.json.JSONObject;

public class TestService extends AutoRegisterService {

    @GET(path="/index", response = "text/text")
    public static void test1(Request request){
        request.getResponse().ok("OK").send();
    }

    @GET(path="/param", response = "text/text")
    public static void test2(Request request, @Param String name, @Param String age) {
        request.getResponse().ok(name+"="+age).send();
    }

    @POST(path="/post", response = "text/text")
    public static void test3(Request request, @Param JSONObject json, @Param String name, @Param String band) {
        request.getResponse().ok(json.get("name") + " plays in " + json.get("band")).send();
    }


}
