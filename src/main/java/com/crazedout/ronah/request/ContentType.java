package com.crazedout.ronah.request;
/*
 * Ronah REST Server
 * Copyright (c) 2026 Fredrik Roos.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * mail: info@crazedout.com
 */

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ContentType {

    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public final static String APPLICATION_XML = "application/xml";
    public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String APPLICATION_JSON = "application/json";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_TEXT = "text/text";
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String IMAGE_JPEG = "image/jpeg";
    public static final String IMAGE_GIF = "image/gif";
    public static final String IMAGE_PNG = "image/png";

    private static final Map<String, String[]> contentTypes = new HashMap<>();

    static{
        init();
    }

    protected String type;
    protected Charset charset = StandardCharsets.UTF_8;

        public static String getContentType(File file, String defaultType) {
            return getContentType(file.getName(),defaultType);
        }

    public static String getContentType(String fileName, String defaultType){
        String res = null;
        String ext;
        System.out.println("'" + fileName + "'");
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

    public ContentType(String type){
        this.type=type;
    }

    public ContentType(String type, Charset charset){
        this.type=type;
        this.charset=charset;
    }

    public void setType(String type){
        this.type = type;
    }

    public void setCharset(Charset charset){
        this.charset=charset;
    }

    public Charset getCharset(){
        return this.charset;
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
