package com.crazedout.ronah;

import com.crazedout.ronah.annotation.GET;
import com.crazedout.ronah.request.HttpRequest;
import com.crazedout.ronah.service.AutoRegisterService;

public class MainTest extends AutoRegisterService {

    @GET(path="/", response = "text/text")
    public void getIndex(HttpRequest request){
        request.getResponse().ok("OK").send();
    }

    public static void main(String[] args){

        new MainTest();
        RonahHttpServer ronah = new RonahHttpServer();
        ronah.start(8080);

    }
}
