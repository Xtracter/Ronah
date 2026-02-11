package com.crazedout.ronah.service;

import com.crazedout.ronah.annotation.CatchAll;
import com.crazedout.ronah.annotation.GET;
import com.crazedout.ronah.annotation.POST;
import com.crazedout.ronah.annotation.Param;
import com.crazedout.ronah.baggins.Baggins;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DefaultService extends AutoRegisterService{

    @CatchAll
    public void catchAll(Request request){
        String html = "<!DOCTYPE html><html><body><h3>Hello from Ronah Catch all</h3></body></html>";
        request.getResponse().ok(html).send();
    }

    @Baggins(name="Echo service 1", description = "Echos query string")
    @GET(path="/", response="text/html", ignoreParentPath = true)
    public void getIndex(Request request){
        System.out.println(request.getQueryString());
        String html = "<!DOCTYPE html><html><body><h3>"+request.getQueryString()+"</h3></body></html>";
        request.getResponse().ok(html).send();
    }

    @Baggins(name="Echo Service 2",description = "Echos the given input name and age")
    @GET(path="/api/v1", response="text/html", ignoreParentPath = true)
    public void getIndex(Request request, @Param String name, @Param String age){
        System.out.println(request.getQueryString());
        String html = String.format("<!DOCTYPE html><html><body><h3>Hello %s %s</h3></body></html>",name,age);
        request.getResponse().ok(html).send();
    }

    @Baggins(name="Echo Service 3",description = "Echos the given input name and age")
    @POST(path="/post", acceptContentType = "application/x-www-form-urlencoded", ignoreParentPath = true)
    public void getRest(Request request, @Param String name, @Param Integer age){
        String html = String.format("<!DOCTYPE html><html><body><h3>Hello from %s %s</h3></body></html>",name,age);
        request.getResponse().ok(html).send();
    }

    @Baggins(name="Echo Service 3",description = "Echos the given json string using name=value")
    @POST(path="/json", response="text/text", acceptContentType = "application/json", ignoreParentPath = true)
    public void getRest2(Request request, @Param String name, @Param Integer age){
        String html = String.format("<!DOCTYPE html><html><body><h3>Hello from JSON %s %s</h3></body></html>", name,age);
        request.getResponse().ok(html).send();
    }

    @POST(path="/json/file", response="text/text", acceptContentType = HttpRequest.OCTET_STREAM, ignoreParentPath = true)
    public void getRest2(Request request) throws IOException {
        try(FileOutputStream fos = new FileOutputStream(new File("c:\\Users\\Admin\\restdemo\\test.pdf"))){
            fos.write(request.getPostData());
        }
        request.getResponse().ok("OK").send();
    }

}
