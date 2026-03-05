package com.crazedout.ronah;

import com.crazedout.ronah.annotation.*;
import com.crazedout.ronah.request.ContentType;
import com.crazedout.ronah.request.HttpRequest;
import com.crazedout.ronah.request.multipart.MultipartPart;
import com.crazedout.ronah.service.AutoRegisterService;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@SuppressWarnings("unused")
@Parent(allowClientIP = {"127.*"})
public class TestService extends AutoRegisterService {

    @API
    @GET(path="/index/*", response = "text/text")
    public static void test(HttpRequest request){
        request.getResponse().ok("OK").send();
    }

    @API
    @GET(path="/index", response = "text/text")
    public static void test1(HttpRequest request){
        request.getResponse().ok("OK").send();
    }

    @API
    @GET(path="/param", response = "text/text")
    public static void test2(HttpRequest request, @Param String name, @Param String age) {
        request.getResponse().ok(name+"="+age).send();
    }

    @API
    @GET(path="/web/*", response = "text/text")
    public static void testWebPath(HttpRequest request) {
        request.getResponse().ok("OK").send();
    }

    @API(suppressParams = {"name","band"}, name = "Ringos Json")
    @POST(path="/post", response = "text/text", acceptContentType = "application/json", enforceParams = true)
    public static void test3(HttpRequest request, @Param JSONObject json, @Param String name, @Param String band) {
        try {
            request.getResponse().ok(json.getString("name") + " plays in " + json.getString("band")).send();
        }catch(JSONException ex){
            request.getResponse().ok(ex.getMessage()).send();
        }
    }

    @API
    @POST(path="/upload", response="text/text", acceptContentType = ContentType.MULTIPART_FORM_DATA)
    public void getRest3(HttpRequest request, @Param String name, @Param String email)  {
        List<MultipartPart> filesPart = request.getMultiParts().stream().filter(MultipartPart::isFile).toList();
        request.getResponse().ok("OK").send();
    }

    @API
    @POST(path="/json", acceptContentType="application/json")
    public void getJson(HttpRequest request, @Param JSONObject json){
        try {
            String response = "Hello " + json.toString();
            request.getResponse().ok(response).send();
        }catch(JSONException ex){
            request.getResponse().error(ex.getMessage()).send();
        }
    }

    @API
    @GET(path="/json", acceptContentType="application/json")
    public void getJson2(HttpRequest request, @Param JSONObject json){
        try {
            String response = "Hello " + json.toString();
            request.getResponse().ok(response).send();
        }catch(JSONException ex){
            request.getResponse().error(ex.getMessage()).send();
        }
    }

    @API
    @GET(path="/person/{name}/{date}/{lastDigits}", response = "text/text")
    public void getPath(HttpRequest request, @Param String name, @Param String date, @Param Integer lastDigits){
            String response = "Hello:" + name + " " + date + " " + (lastDigits+1);
            request.getResponse().ok(response).send();
    }
    /*@API
    @POST(path="/cors", acceptContentType="application/json", allowCORSOrigins = {"http://mytest.org"})
    public void getCORS(Request request, @Param JSONObject json){
        try {
            String response = "Hello " + json.toString();
            request.getResponse().ok(response).send();
        }catch(JSONException ex){
            request.getResponse().error(ex.getMessage()).send();
        }
    }*/

    public static void main(String[] args){
        new TestService();
        RonahHttpServer server = new RonahHttpServer();
        System.out.println("http://localhost:8083/api");
        server.start(8083);
    }

}
