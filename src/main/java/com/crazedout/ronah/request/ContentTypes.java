package com.crazedout.ronah.request;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ContentTypes {

    private ContentTypes(){

    }

    private static final Map<String, String[]> contentTypes = new HashMap<>();

    static{
        init();
    }

    public static String getContentType(File file, String defaultType) {
        return getContentType(file.getName(),defaultType);
    }

    public static String getContentType(String fileName, String defaultType){
        String ext;
        int i = fileName.lastIndexOf(".");
        if(i!=-1){
            ext = fileName.substring(i+1);
            for (Map.Entry<String, String[]> entry : contentTypes.entrySet()) {
                for(String e:entry.getValue()){
                    if(ext.equalsIgnoreCase(e)) return entry.getKey();
                }
            }
        }
        return defaultType;
    }

    public static void addContentType(String contentType, String... fileExtension){
        contentTypes.put(contentType, fileExtension);
    }

    private static void init(){
        addContentType("application/font-woff", "woff");
        addContentType("application/font-woff2", "woff2");
        addContentType("application/java-archive", "jar");
        addContentType("application/javascript", "js");
        addContentType("application/json", "json");
        addContentType("application/octet-stream", "exe");
        addContentType("application/pdf", "pdf");
        addContentType("application/x-7z-compressed", "7z");
        addContentType("application/x-compressed", "tgz");
        addContentType("application/x-gzip", "gz");
        addContentType("application/x-tar", "tar");
        addContentType("application/xhtml+xml", "xhtml");
        addContentType("application/zip", "zip");
        addContentType("audio/mpeg", "mp3");
        addContentType("image/gif", "gif");
        addContentType("image/jpeg", "jpg", "jpeg");
        addContentType("image/png", "png");
        addContentType("image/svg+xml", "svg");
        addContentType("image/x-icon", "ico");
        addContentType("text/css", "css");
        addContentType("text/csv", "csv");
        addContentType("text/html; charset=utf-8", "htm", "html");
        addContentType("text/plain", "txt", "text", "log");
        addContentType("text/xml", "xml");
    }

}
