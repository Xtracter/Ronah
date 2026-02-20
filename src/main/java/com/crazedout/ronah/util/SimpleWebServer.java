package com.crazedout.ronah.util;

import com.crazedout.ronah.request.ContentType;
import com.crazedout.ronah.request.Request;
import com.crazedout.ronah.service.AutoRegisterService;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public abstract class SimpleWebServer extends AutoRegisterService {

    private final String basePath;

    public SimpleWebServer(String basePath){
        super();
        this.basePath = basePath;
    }

    public SimpleWebServer(){
        super();
        this.basePath = System.getProperty("user.dir");
    }

    protected String[] defaultPage = {"index.html","index.htm","index.php"};

    public void doGet(Request request){

        String contextPath = request.getPath().substring("/web".length());

        String file = "";
        int i = contextPath.lastIndexOf("/");
        if(i!=-1){
            file = contextPath.substring(i);
            if('/'==file.charAt(0)) file = file.substring(1);
        }
        contextPath = contextPath.substring(0,contextPath.length()-file.length());

        String systemFile = getRequestedFile(basePath + contextPath, file);
        if(systemFile==null){
            request.getResponse().notFound("File was null").send();
            return;
        }
        String ct = ContentType.getContentType(new File(systemFile).getName(),"text/text");
        try(DataInputStream in = new DataInputStream(new FileInputStream(systemFile))){
            byte[] buffer = in.readAllBytes();
            request.getResponse().contentType(ct).ok(buffer).send();
        }catch(Exception ex){
            request.getResponse().notFound(ex.getMessage()).send();
        }
    }

    private String getRequestedFile(String filePath, String file){
        File f;
        if(file.isEmpty()){
            for (String s : defaultPage) {
                f = new File((filePath + File.separatorChar + s).replace("/", "\\"));
                if (f.exists()) return f.getAbsolutePath();
            }
        }else{
            f  = new File((filePath + file).replace("/","\\"));
            if(f.exists()) return f.getAbsolutePath();
        }
        return null;
    }
}
