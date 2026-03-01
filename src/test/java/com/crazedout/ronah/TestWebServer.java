package com.crazedout.ronah;

import com.crazedout.ronah.annotation.API;
import com.crazedout.ronah.annotation.GET;
import com.crazedout.ronah.request.ContentTypes;
import com.crazedout.ronah.request.HttpRequest;
import com.crazedout.ronah.request.Request;
import com.crazedout.ronah.util.SimpleWebServer;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import static com.crazedout.ronah.auth.BasicAuthentication.addUser;

public class TestWebServer extends SimpleWebServer {

    public TestWebServer(String basePath){
        super();
        setBasePath(basePath);
        addUser("falcon","pencil");
    }

    @Override
    public String getName(){
        return "TestWebServer";
    }

    @API
    @GET(path="/web/*")
    public void testWebServer(Request request){
        File file = super.getFile((HttpRequest) request,"/web");
        sendFile(request,file);
    }

    void sendFile(Request request, File file){
        if(file!=null) {
            String ct = ContentTypes.getContentType(file.getName(), "text/text");
            try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
                byte[] buffer = in.readAllBytes();
                request.getResponse().contentType(ct).ok(buffer).send();
            } catch (Exception ex) {
                request.getResponse().notFound(ex.getMessage()).send();
            }
        }else {
            request.getResponse().notFound().send();
        }
    }

    public static void main(String[] args){

        new TestWebServer("src/test/web");
        System.out.println("http://localhost:8080/web");
        RonahHttpServer r = new RonahHttpServer();
        r.start(8080);
    }

}
