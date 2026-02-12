package com.crazedout.ronah.service;

import com.crazedout.ronah.annotation.*;
import com.crazedout.ronah.api.API;
import com.crazedout.ronah.service.handler.MultipartPart;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@SuppressWarnings("unused")
public class DefaultService extends AutoRegisterService{

    public DefaultService(){
        super();
        BasicAuthentication.addUser("roos","roos" );
    }

    @CatchAll
    public void catchAll(Request request){
        String html = "<!DOCTYPE html><html><body><h3>Hello from Ronah Catch all</h3></body></html>";
        request.getResponse().ok(html).send();
    }

    @GET(path="/secret",response = "text/html", useBasicAuth = true, basicAuthRealm = "roos")
    public void getSecret(Request request) {
        request.getResponse().ok("OK").send();
    }

    @GET(path="/form",response = "text/html")
    public void getFile(Request request) throws IOException{
        DataInputStream dis = new DataInputStream(Objects.requireNonNull(getClass().getResourceAsStream("/form.html")));
        byte[] buffer = dis.readAllBytes();
        request.getResponse().ok(new String(buffer)).send();
    }

    @API(name="Echo service 1", description = "Echos query string")
    @GET(path="/", response="text/html", ignoreParentPath = true)
    public void getIndex(Request request){
        System.out.println(request.getQueryString());
        String html = "<!DOCTYPE html><html><body><h3>"+request.getQueryString()+"</h3></body></html>";
        request.getResponse().ok(html).send();
    }

    @API(name="Echo Service 2",description = "Echos the given input name and age")
    @GET(path="/api/v1", response="text/html", ignoreParentPath = true)
    public void getIndex(Request request, @Param String name, @Param String age){
        System.out.println(request.getQueryString());
        String html = String.format("<!DOCTYPE html><html><body><h3>Hello %s %s</h3></body></html>",name,age);
        request.getResponse().ok(html).send();
    }

    @API(name="Echo Service 3",description = "Echos the given input name and age")
    @POST(path="/post", acceptContentType = "application/x-www-form-urlencoded", ignoreParentPath = true)
    public void getRest(Request request, @Param String name, @Param Integer age){
        String html = String.format("<!DOCTYPE html><html><body><h3>Hello from %s %s</h3></body></html>",name,age);
        request.getResponse().ok(html).send();
    }

    @API(name="Echo Service 3",description = "Echos the given json string using name=value")
    @POST(path="/json", response="text/text", acceptContentType = "application/json", ignoreParentPath = true)
    public void getRest2(Request request, @Param String name, @Param Integer age){
        String html = String.format("<!DOCTYPE html><html><body><h3>Hello from JSON %s %s</h3></body></html>", name,age);
        request.getResponse().ok(html).send();
    }

    @API
    @POST(path="/post/file", response="text/text", acceptContentType = HttpRequest.MULTIPART_FORM_DATA, ignoreParentPath = true)
    public void getRest3(Request request)  {
        String res = "";
        for(MultipartPart part:request.getMultiParts()){
            res += part.getHeader("Content-Type") + "/" + part.getHeader("Content-Disposition") + "\n";
        }
        request.getResponse().ok(res).send();
    }

    @POST(path="/json/upload", response="text/text", acceptContentType = HttpRequest.OCTET_STREAM, ignoreParentPath = true)
    public void getRest2(Request request) throws IOException {
        try(FileOutputStream fos = new FileOutputStream("test.pdf")){
            fos.write(request.getPostData());
        }
        request.getResponse().ok("OK").send();
    }

}
