package com.crazedout.ronah;

import com.crazedout.ronah.annotation.API;
import com.crazedout.ronah.annotation.GET;
import com.crazedout.ronah.request.HttpRequest;
import com.crazedout.ronah.util.SimpleWebServer;

import java.io.*;

public class TestWebServer extends SimpleWebServer {

    public TestWebServer(String basePath){
        super();
        setBasePath(basePath);
    }

    @Override
    public String getName(){
        return "TestWebServer";
    }

    @API(suppressParams = {"request"})
    @GET(path="/web/*", response = "text/html")
    public void testWebServer(HttpRequest request){
        File file = super.getFile(request);
        sendFile(request,file);
    }

    void sendFile(HttpRequest request, File file){
        if(file!=null) {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                String line;
                while((line=r.readLine())!=null){
                    sb.append(line).append("\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            request.getResponse().ok(sb.toString()).send();
        }else {
            request.getResponse().notFound().send();
        }
    }

    public static void main(String[] args){
        new TestWebServer("src/test");
        System.out.println("http://localhost:8080/web");
        RonahHttpServer r = new RonahHttpServer();
        r.start(8080);
    }

}
