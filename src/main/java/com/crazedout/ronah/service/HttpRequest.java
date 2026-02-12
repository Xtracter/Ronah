package com.crazedout.ronah.service;
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
import com.crazedout.ronah.annotation.BasicAuthentication;
import com.crazedout.ronah.service.handler.MultipartPart;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to hold an HTTP request.
 */
@SuppressWarnings("unused")
public class HttpRequest implements Request {

    private Charset defaultCharset = StandardCharsets.UTF_8;
    private String protocol;
    private String method;
    private String path;
    private String queryString;
    private final Map<String,String> headers;
    private final Response response;
    private byte[] postData;
    private List<MultipartPart> multipartParts;
    private BasicAuthentication.BasicUser basicUser;

    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String APPLICATION_JSON = "application/json";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTML = "text/html";
    public static final String OCTET_STREAM = "application/octet-stream";

    /**
     * Constructor.
     * @param httpLine first line of an HTTP request.
     * @param out OutputStream to write to client.
     */
    public HttpRequest(String httpLine, InputStream in, OutputStream out){
        this.headers = new HashMap<>();
        this.response = new HttpResponse(out);
        this.parse(httpLine);
    }

    @Override
    public void setMultiParts(List<MultipartPart> multiParts){
        this.multipartParts=multiParts;
    }

    @Override
    public List<MultipartPart> getMultiParts(){
        return this.multipartParts;
    }

    /**
     * Parses the first line of an HTTP request for method,protocol,path and query string.
     * @param line String first line of an http request.
     */
    protected void parse(String line){
        String[] tokens = line.split(" ");
        this.method = tokens[0];
        this.protocol = tokens[2];
        if(tokens[1].contains("?")) {
            String[] split = tokens[1].split("\\?");
            this.path = split[0];
            this.queryString = split[1];
        }else{
            this.path = tokens[1];
        }
    }

    public void setUser(User user){
        this.basicUser= (BasicAuthentication.BasicUser) user;
    }

    @Override
    public User getUser(){
        return this.basicUser;
    }

    /**
     * Gets a Http Header value
     * @param key String header key ignore case.
     * @return String value
     */
    public String getHeader(String key) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if(entry.getKey().equalsIgnoreCase(key)){
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Saves the posted bytes to file.
     * @param file File output file.
     * @throws IOException Exception.
     */
    public void savePostDataToFile(String file) throws IOException {
        try(FileOutputStream fos = new FileOutputStream(file)){
            fos.write(this.getPostData());
        }
    }

    /**
     * Sets the Query String
     * @param queryString String query
     */
    void setQueryString(String queryString){
        this.queryString=queryString;
    }

    /**
     * Sets the Charset for this Request.
     * @param charset Charset
     */
    @Override
    public void setCharset(Charset charset){
        this.defaultCharset=charset;
    }

    /**
     * Gets a HTTP query string parameter using default CharSet
     * @param key name of the parameter.
     * @return value of the parameter.
     */
    @Override
    public String getParameter(String key){
        if(queryString!=null){
            String[] tokens = queryString.split("&");
            for(String t:tokens){
                String[] pair = t.split("=");
                if(key.equals(URLEncoder.encode(pair[0],defaultCharset)))
                    return URLDecoder.decode(pair[1],defaultCharset);
            }
        }
        return null;
    }

    public List<String> getParameterArray(String key){
        return null;
    }

    /**
     * Gets a HTTP query string parameter using CharSet
     * @param key name of the parameter.
     * @param charSet Charset
     * @return value of the parameter.
     */
    @Override
    public String getParameter(String key, Charset charSet){
        if(queryString!=null){
            String[] tokens = queryString.split("&");
            for(String t:tokens){
                String[] pair = t.split("=");
                if(key.equals(URLEncoder.encode(pair[0],charSet)))
                    return URLDecoder.decode(pair[1],charSet);
            }
        }
        return null;
    }

    /**
     * Returns a string reprensation of the HttpRequest.
     * @return String
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Method:").append(method).append(", Protocol:").
                append(protocol).append(", Path:").append(path);

        for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
            sb.append(entry.getKey()).append(":");
            sb.append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Set POST data
     * @param data post data
     */
    @Override
    public void setPostData(byte[] data){
        this.postData=data;
    }

    /**
     * Gets the POST data.
     * @return POST data
     */
    @Override
    public byte[] getPostData(){
        return this.postData;
    }

    /**
     * Gets the path of this HTTP request.
     * @return String path of request.
     */
    @Override
    public String getPath() {
        return this.path;
    }

    /**
     * Returns a Map of the HTTP headers from client.
     * @return Map of headers.
     */
    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    /**
     * Gets the Response onbject of this HttpRequest.
     * @return Response.
     */
    @Override
    public Response getResponse() {
        return this.response;
    }

    /**
     * Get the HTTP protocol for this request.
     * @return String protocol.
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Gets the request method for this request.
     * @return String request method.
     */
    public String getMethod() {
        return method;
    }

    /**
     * Gets the query string for this request - if existing.
     * @return String query string.
     */
    public String getQueryString() {
        return queryString;
    }

}
