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

import com.crazedout.ronah.Ronah;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to handle an HTTP response.
 */
@SuppressWarnings("unused")
public class HttpResponse implements Response{

    private final OutputStream out;
    private final StringBuilder builder;
    private String data;
    private final SimpleDateFormat dateFormat;
    private final Map<String, String> userHeaders;

    public final static String CONTENT_TYPE_JSON = "application/json";
    public final static String CONTENT_TYPE_HTML = "text/html";
    public final static String CONTENT_TYPE_TEXT = "text/text";
    public final static String CONTENT_TYPE_PNG = "image/png";
    public final static String CONTENT_TYPE_JPG = "image/jpeg";
    public final static String CONTENT_TYPE_GIF = "image/gif";
    public final static String CONTENT_TYPE_XML = "application/xml";

    private String contentType = CONTENT_TYPE_TEXT;

    /**
     * Constructor.
     * @param out HTTP output stream.
     */
    HttpResponse(OutputStream out){
        this.out=out;
        this.builder = new StringBuilder();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this.userHeaders = new HashMap<>();
    }

    /**
     * Creates an HTTP 404 response.
     * @return Response
     */
    public Response notFound(){
        this.builder.append("HTTP/1.1 404 Not Found\n");
        this.data = "Resource was not found";
        return this;
    }

    /**
     * Creates an HTTP 500 response.
     * @return Response
     */
    public Response error(){
        this.data = "HTTP/1.1 500 Internal Server";
        this.builder.append("HTTP/1.1 500 Internal Error\n");
        this.data = "Internal Server Error";
        return this;
    }

    /**
     * Creates an HTTP 404 response.
     * @return Response
     */
    public Response forbidden(){
        this.builder.append("HTTP/1.1 403 Forbidden\n");
        this.data = "Resource is not allowed";
        return this;
    }

    /**
     * Creates an HTTP 200 OK response.
     * @param data String response content.
     * @return Response
     */
    public Response ok(String data){
        this.builder.append("HTTP/1.1 200 OK\n");
        this.data = data;
        return this;
    }

    /**
     * Creates an HTTP 200 OK response.
     * @param contentType String content type of response.
     * @param data String response content.
     * @return Response
     */
    public Response ok(String contentType, String data){
        this.contentType = contentType;
        this.data=data;
        this.builder.append("HTTP/1.1 200 OK\n");
        return this;
    }

    /**
     * Gets the Response's output stream,
     * @return OutputStream
     */
    @Override
    public OutputStream getOutputStream() {
        return this.out;
    }

    /**
     * Sends the response.
     */
    public void send() {
        try {
            this.builder.append("Server: ").append(Ronah.server).append(" ").append(Ronah.version).append("\n");
            this.builder.append("Date: ").append(dateFormat.format(new Date())).append("\n");
            this.builder.append("Content-Type: ").append(this.contentType).append("\n");
            this.builder.append("Content-Length: ").append(data.length()).append("\n");
            for (Map.Entry<String, String> entry : userHeaders.entrySet()) {
                this.builder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            this.builder.append("Connection: close\n\n");
            out.write(this.builder.toString().getBytes());
            this.out.write(data.getBytes());
        }catch(IOException ex){
            internalError(ex.getMessage());
        }
    }

    /**
     * Sends an 500 Internal error.
     * @param message String message of the error.
     */
    public void internalError(String message) {
        try {
            out.write(("HTTP/1.1 500 Internal Error\n").getBytes());
            out.write(("Content-Type: text/text\n").getBytes());
            out.write(("Content-Length: " + message.length() + "\n").getBytes());
            out.write("\n".getBytes());
            out.write(message.getBytes());
        }catch(IOException ex){
            ex.printStackTrace(System.out);
        }
    }

    /**
     * Sets the content type for this response.
     * @param contentType String content type.
     */
    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Sets the POST data for this response if viable.
     * @param data String data.
     */
    @Override
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Adds an HTTP header to this response
     * @param key header name.
     * @param value header value.
     */
    @Override
    public void addHeader(String key, String value){
        this.userHeaders.put(key,value);
    }
}
