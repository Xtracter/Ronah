package com.crazedout.ronah;

import com.crazedout.ronah.annotation.*;
import com.crazedout.ronah.api.APIService;
import com.crazedout.ronah.request.ContentType;
import com.crazedout.ronah.service.AutoRegisterService;
import com.crazedout.ronah.request.Request;
import com.crazedout.ronah.request.multipart.MultipartPart;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@SuppressWarnings("unused")
@Parent(allowClientIP = {"127.*"})
public class TestService extends AutoRegisterService {

    @API
    @GET(path="/index/*", response = "text/text")
    public static void test(Request request){
        request.getResponse().ok("OK").send();
    }

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

    @API(suppressParams = {"name","band"})
    @POST(path="/post", response = "text/text", acceptContentType = "application/json")
    public static void test3(Request request, @Param JSONObject json, @Param String name, @Param String band) {
        request.getResponse().ok(json.getString("name") + " plays in " + json.getString("band")).send();
    }

    @API
    @POST(path="/upload", response="text/text", acceptContentType = ContentType.MULTIPART_FORM_DATA)
    public void getRest3(Request request, @Param String name, @Param String email)  {
        System.out.println("Here");
        List<MultipartPart> filesPart = request.getMultiParts().stream().filter(MultipartPart::isFile).toList();
        request.getResponse().ok("OK").send();
    }

    @API
    @POST(path="/json", acceptContentType="application/json")
    public void getJson(Request request, @Param JSONObject json){
        try {
            String response = "Hello " + json.toString();
            request.getResponse().ok(response).send();
        }catch(JSONException ex){
            request.getResponse().error(ex.getMessage()).send();
        }
    }

    @API
    @GET(path="/json", acceptContentType="application/json")
    public void getJson2(Request request, @Param JSONObject json){
        try {
            String response = "Hello " + json.toString();
            request.getResponse().ok(response).send();
        }catch(JSONException ex){
            request.getResponse().error(ex.getMessage()).send();
        }
    }

    @API
    @POST(path="/cors", acceptContentType="application/json", allowCORSOrigins = {"http://mytest.org"})
    public void getCORS(Request request, @Param JSONObject json){
        try {
            String response = "Hello " + json.toString();
            request.getResponse().ok(response).send();
        }catch(JSONException ex){
            request.getResponse().error(ex.getMessage()).send();
        }
    }

    public static void main(String[] args){
        new TestService();
        new APIService();
        RonahHttpServer server = new RonahHttpServer();
        System.out.println("http://localhost:8083/api");
        server.start(8083);
    }

}
