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
import com.crazedout.ronah.RonahHttpServer;
import com.crazedout.ronah.auth.BasicAuthentication;
import com.crazedout.ronah.auth.User;
import com.crazedout.ronah.request.multipart.MultipartPart;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to hold an HTTP request.
 */
@SuppressWarnings("unused")
public class HttpRequest  {

    private String protocol;
    private String method;
    private String path;
    private String queryString;
    private final Map<String,String> headers;
    private final HttpResponse response;
    private byte[] postData;
    private List<MultipartPart> multipartParts;
    private BasicAuthentication.BasicUser basicUser;
    private final Charset charset = StandardCharsets.UTF_8;
    private ContentType contentType;
    private final InetSocketAddress socketAddress;

    /**
     * Constructor.
     * @param httpLine first line of an HTTP request.
     * @param out OutputStream to write to client.
     */
    public HttpRequest(InetSocketAddress socketAddress, String httpLine, InputStream in, OutputStream out){
        this.headers = new HashMap<>();
        this.response = new HttpResponse(out);
        this.socketAddress = socketAddress;
        this.parse(httpLine);
    }

    public InetSocketAddress getSocketAddress(){
        return this.socketAddress;
    }


    public void setMultiParts(List<MultipartPart> multiParts){
        this.multipartParts=multiParts;
    }

    public List<MultipartPart> getMultiParts(){
        return this.multipartParts;
    }

    /**
     * Parses the first line of an HTTP request for method,protocol,path and query string.
     * @param line String first line of an http request.
     */
    protected void parse(String line){
        System.out.println(line);
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

    public ContentType getContentType(){
        return this.contentType;
    }

    public void parseHeader(String key, String value){
        if(value.contains(";")){
            String[] vp = value.split(";");
            if(vp.length>0 && key.equalsIgnoreCase("content-type")){
                this.headers.put(key,vp[0].trim());
                if(vp[1].toLowerCase().contains("charset=")){
                    String[] cs = vp[1].split("=");
                    try {
                        this.contentType = new ContentType(vp[0], Charset.forName(cs[1]));
                    }catch (UnsupportedCharsetException ex){
                        RonahHttpServer.logger.warning("Bad Charset in Content-Type:" + cs[1]);
                    }
                }else {
                    this.contentType = new MultipartContentType(vp[1].trim());
                }
            }
        }else{
            this.headers.put(key,value);
        }
    }

    /**
     * Sets the User for this Request.
     * @param user User.
     */
    public void setUser(User user){
        this.basicUser= (BasicAuthentication.BasicUser) user;
    }

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
     * Adds a query String parameter.
     * @param key String key
     * @param value String value
     */
    public void addParameter(String key, String value){
        this.queryString+="&" + key + "=" + value;
    }

    /**
     * Sets the Query String
     * @param queryString String query
     */
    public void setQueryString(String queryString){
        this.queryString=queryString;
    }

    /**
     * Gets a HTTP query string parameter using default CharSet
     * @param key name of the parameter.
     * @return value of the parameter.
     */
    public String getParameter(String key){
        try {
            if (queryString != null) {
                String[] tokens = queryString.split("&");
                for (String t : tokens) {
                    String[] pair = t.split("=");
                    if (key.equalsIgnoreCase(URLEncoder.encode(pair[0], charset)))
                        return URLDecoder.decode(pair[1], charset);
                }
            }
        }catch(ArrayIndexOutOfBoundsException ex){
            ex.printStackTrace(System.out);
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
    public void setPostData(byte[] data){
        this.postData=data;
    }

    /**
     * Gets the POST data.
     * @return POST data
     */
    public byte[] getPostData(){
        return this.postData;
    }

    /**
     * Gets the path of this HTTP request.
     * @return String path of request.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Returns a Map of the HTTP headers from client.
     * @return Map of headers.
     */
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    /**
     * Gets the Response object of this HttpRequest.
     * @return Response.
     */
    public HttpResponse getResponse() {
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
