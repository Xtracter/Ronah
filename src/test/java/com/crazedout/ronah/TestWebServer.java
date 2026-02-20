package com.crazedout.ronah;

import com.crazedout.ronah.annotation.API;
import com.crazedout.ronah.annotation.GET;
import com.crazedout.ronah.request.HttpRequest;
import com.crazedout.ronah.request.Request;
import com.crazedout.ronah.util.SimpleWebServer;

import static com.crazedout.ronah.auth.BasicAuthentication.addUser;

public class TestWebServer extends SimpleWebServer {

    public TestWebServer(String basePath){
        super(basePath);
        addUser("falcon","pencil");
    }

    @API
    @GET(path="/web/*")
    public void testWebServer(Request request){
        System.out.println("1:" + request.getPath());
        super.doGet((HttpRequest) request);
    }

    public static void main(String[] args){

        new TestWebServer("src/test/web");
        System.out.println("http://localhost:8080/web");
        RonahHttpServer r = new RonahHttpServer();
        r.start(8080);
    }

}
