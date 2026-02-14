package com.crazedout.ronah;

import com.crazedout.ronah.annotation.GET;
import com.crazedout.ronah.annotation.POST;
import com.crazedout.ronah.annotation.Param;
import com.crazedout.ronah.api.API;
import com.crazedout.ronah.api.APIService;
import com.crazedout.ronah.service.AutoRegisterService;
import com.crazedout.ronah.service.HttpRequest;
import com.crazedout.ronah.service.Request;
import com.crazedout.ronah.service.RonahHttpServer;
import com.crazedout.ronah.service.handler.MultipartPart;
import org.json.JSONObject;

import java.util.List;

@SuppressWarnings("unused")
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
    @POST(path="/post", response = "text/text", acceptContentType = "application/json")
    public static void test3(Request request, @Param JSONObject json, @Param String name, @Param String band) {
        request.getResponse().ok(json.getString("name") + " plays in " + json.getString("band")).send();
    }

    @API
    @POST(path="/upload", response="text/text", acceptContentType = HttpRequest.MULTIPART_FORM_DATA)
    public void getRest3(Request request, @Param String name, @Param String email)  {
        List<MultipartPart> filesPart = request.getMultiParts().stream().filter(MultipartPart::isFile).toList();
        request.getResponse().ok("OK").send();
    }


    public static void main(String[] args){
        new TestService();
        new APIService();
        RonahHttpServer server = new RonahHttpServer();
        System.out.println("http://localhost:8083/api");
        server.start(8083);
    }

}
