package com.crazedout.ronah;

import com.crazedout.ronah.annotation.API;
import com.crazedout.ronah.annotation.GET;
import com.crazedout.ronah.request.HttpRequest;
import com.crazedout.ronah.util.SimpleWebServer;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Main extends SimpleWebServer {

    SimpleWebServer web;

    public Main(){
        super();
        setBasePath("C:/Users/Admin/Desktop/c8soft/");
    }

    @GET(path="/web/*")
    public void aServer(HttpRequest request){
        File f = getFile(request,"/web");
        if(f!=null && f.exists()){
            try(DataInputStream din = new DataInputStream(new FileInputStream(f))){
                byte[] buffer = din.readAllBytes();
                request.getResponse().ok(buffer).send();
            }catch(IOException ex){
                request.getResponse().error(ex.getMessage()).send();
            }
        }else{
            request.getResponse().notFound().send();
        }
        request.getResponse().ok("OK").send();
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