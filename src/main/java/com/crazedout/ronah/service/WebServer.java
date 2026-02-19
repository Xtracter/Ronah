package com.crazedout.ronah.service;

import com.crazedout.ronah.annotation.GET;
import com.crazedout.ronah.annotation.POST;
import com.crazedout.ronah.request.Request;

import java.io.*;

public class WebServer extends AutoRegisterService{

    String basePath;

    public WebServer(String baseBath){
        this.basePath = baseBath;
    }

    String readFile(File file) throws IOException {
        try(BufferedReader r = new BufferedReader(new FileReader(file))){
            String line;
            StringBuilder sb = new StringBuilder();
            while((line=r.readLine())!=null){
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    @GET(path="/web/*")
    public void doGet(Request request) throws IOException{
        String contextPath = request.getPath().substring(5);
        File file = new File(basePath + File.separator + contextPath);
        if(file.exists()){
            String html = readFile(file);
            request.getResponse().contentType("text/html").ok(html).send();
        }else{
            request.getResponse().notFound().send();
        }
    }

    @POST(path="/web/post/*")
    public void doPost(Request request) throws IOException{
        String contextPath = request.getPath().substring(5);
        File file = new File(basePath + File.separator + contextPath);
        if(file.exists()){
            String html = readFile(file);
            request.getResponse().contentType("text/html").ok(html).send();
        }else{
            request.getResponse().notFound().send();
        }
    }
}
