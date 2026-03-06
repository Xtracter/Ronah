package com.crazedout.ronah;

import com.crazedout.ronah.annotation.API;
import com.crazedout.ronah.annotation.GET;
import com.crazedout.ronah.request.HttpRequest;
import com.crazedout.ronah.util.SimpleWebServer;

import java.io.*;

public class Main extends SimpleWebServer {

    SimpleWebServer web;

    public Main(){
        super();
        setBasePath("C:/Users/Admin/Desktop/c8soft/");
    }

    @GET(path="/web/*", response = "text/html")
    public void aServer(HttpRequest request){
        File f = getFile(request,"");
        if(f!=null && f.exists()){
            StringBuilder sb = new StringBuilder();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f)))) {
                String line;
                while((line=r.readLine())!=null){
                    sb.append(line).append("\n");
                }
            } catch (IOException ex) {
                request.getResponse().error(ex.getMessage()).send();
            }
            request.getResponse().ok(sb.toString()).send();
        }else{
            request.getResponse().notFound().send();
        }
    }

    @API
    @GET(path="/get/person", response = "text/html")
    public void get(HttpRequest request){
        request.getResponse().ok("<html><body><h2>"+request.getParameter("pnr12")+"</h2></body></html>").send();
    }

    @API
    @GET(path="/get/person/*", response = "text/html")
    public void get2(HttpRequest request){
        request.getResponse().ok("<html><body><h2>"+request.getParameter("pnr12")+"</h2></body></html>").send();
    }

    public static void main(String[] args) {

        new Main();
        System.out.println("http://localhost:8080/api");
        RonahHttpServer r = new RonahHttpServer();
        r.start(8080);

    }
}