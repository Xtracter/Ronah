package com.crazedout.ronah;

import com.crazedout.ronah.annotation.GET;
import com.crazedout.ronah.annotation.POST;
import com.crazedout.ronah.annotation.Param;
import com.crazedout.ronah.api.API;
import com.crazedout.ronah.api.APIService;
import com.crazedout.ronah.service.AutoRegisterService;
import com.crazedout.ronah.service.DefaultService;
import com.crazedout.ronah.service.Request;
import com.crazedout.ronah.service.RonahHttpServer;
import org.json.JSONObject;

public class TestService extends AutoRegisterService {

    @API
    @GET(path="/index", response = "text/text")
    public static void test1(Request request){
        request.getResponse().ok("OK").send();
    }

    @API
    @GET(path="/param", response = "text/text")
    public static void test2(Request request, @Param String name, @Param String age) {
        request.getResponse().ok(name+"="+age).send();
    }

    @API
    @POST(path="/post", response = "text/text")
    public static void test3(Request request, @Param JSONObject json, @Param String name, @Param Integer age) {
        request.getResponse().ok(name + " " + age).send();
    }

    public static void main(String[] args){
        new TestService();
        new APIService();
        RonahHttpServer server = new RonahHttpServer();
        System.out.println("http://localhost:8083/api");
        server.start(8083);
    }

}
