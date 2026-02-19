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
import com.crazedout.ronah.request.cors.CORS;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class to handle an HTTP response.
 */
@SuppressWarnings("unused")
public class HttpResponse implements Response{

    private final OutputStream out;
    private final StringBuilder builder;
    private byte[] data;
    private final SimpleDateFormat dateFormat;
    private final Map<String, String> userHeaders;
    private Charset charset = StandardCharsets.UTF_8;

    private String contentType = ContentType.TEXT_HTML;

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
        this.contentType=ContentType.TEXT_HTML;
        this.data =
                "<!DOCTYPE html><html><body><h3>HTTP/1.1 404 Not Found</h2>Resource was not found</h3></body></html>".getBytes(charset);
        return this;
    }

    /**
     * Creates an HTTP 401 response.
     * @return Response
     */
    public Response auth(String realm){
        userHeaders.put("WWW-Authenticate", String.format("Basic realm=\"%s\"",realm));
        this.builder.append("HTTP/1.1 401 Unauthorized\n");
        this.contentType=ContentType.TEXT_TEXT;
        this.data =
                "<!DOCTYPE html><html><body><h3>HTTP/1.1 401 Unauthorized</h2>Authentication required</h3></body></html>".getBytes(charset);
        return this;
    }

    /**
     * Creates an HTTP 500 response.
     * @return Response
     */
    public Response error(){
        this.builder.append("HTTP/1.1 500 Internal Server\n");
        this.contentType=ContentType.TEXT_HTML;
        this.data =
                "<!DOCTYPE html><html><body><h3>HTTP/1.1 500 Internal Server</h3></body></html>\n".getBytes(charset);
        return this;
    }

    /**
     * Creates an HTTP 500 response.
     * @return Response
     */
    public Response error(String message){
        this.builder.append("HTTP/1.1 500 Internal Server\n");
        this.contentType=ContentType.TEXT_HTML;
        this.data =
                String.format("<!DOCTYPE html><html><body><h3>HTTP/1.1 500 Internal Server</h3>"+message+"</body></html>\n").getBytes(charset);
        return this;
    }

    /**
     * Creates an HTTP 404 response.
     * @return Response
     */
    public Response forbidden(){
        this.builder.append("HTTP/1.1 403 Forbidden\n");
        this.contentType=ContentType.TEXT_HTML;
        this.data = "<!DOCTYPE html><html><body><h3>HTTP/1.1 403 Forbidden</h3></body></html>\n".getBytes(charset);
        return this;
    }

    /**
     * Sets the char set for this response.
     * @param charset Charset
     */
    public Response charset(Charset charset){
        this.charset = charset;
        return this;
    }

    /**
     * Creates an HTTP 200 OK response.
     * @param data String response content.
     * @return Response
     */
    public Response ok(String data){
        this.builder.append("HTTP/1.1 200 OK\n");
        this.data = data.getBytes(charset);
        return this;
    }

    /**
     * Creates an HTTP 200 OK response.
     * @param data byte[] response content.
     * @return Response
     */
    public Response ok(byte[] data){
        this.builder.append("HTTP/1.1 200 OK\n");
        this.data = data;
        return this;
    }

    /**
     * Adding default CORS headers.
     * @param allow clients to allow.
     */
    public void applyCORSHeaders(Request request, String... allow){
        CORS.getCORSHeaders(request,allow);
    }

    /** Sets the Content-Type header for this Response.
     * @param contentType String Content type.
     * @return Response
     */
    @Override
    public Response contentType(String contentType){
        this.contentType = contentType;
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

    public void sendOptions(){
        try {
            this.builder.append("Server: ").append(RonahHttpServer.server).append(" ").append(RonahHttpServer.version).append("\n");
            this.builder.append("Date: ").append(dateFormat.format(new Date())).append("\n");
            this.builder.append("Allow: GET,POST,OPTIONS\n");
            for (Map.Entry<String, String> entry : userHeaders.entrySet()) {
                this.builder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            this.builder.append("Connection: close\n\n");
            out.write(this.builder.toString().getBytes(charset));
        }catch(IOException ex){
            ex.printStackTrace(System.out);
            internalError(ex.getMessage());
        }
    }

    /**
     * Sends the response.
     */
    public void send() {
        try {
            this.builder.append("Server: ").append(RonahHttpServer.server).append(" ").append(RonahHttpServer.version).append("\n");
            this.builder.append("Date: ").append(dateFormat.format(new Date())).append("\n");
            this.builder.append("Allow: GET,POST,OPTIONS\n");
            this.builder.append("Content-Type: ").append(this.contentType).append("; Charset=").append(charset).append("\n");
            this.builder.append("Content-Length: ").append(data.length).append("\n");
            for (Map.Entry<String, String> entry : userHeaders.entrySet()) {
                this.builder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            this.builder.append("Connection: close\n\n");
            out.write(this.builder.toString().getBytes(charset));
            this.out.write(data);
        }catch(IOException ex){
            ex.printStackTrace(System.out);
            internalError(ex.getMessage());
        }
    }

    /**
     * Sends an 500 Internal error.
     * @param message String message of the error.
     */
    public void internalError(String message) {
        try {
            out.write(("HTTP/1.1 500 Internal Error\n").getBytes(charset));
            out.write(("Content-Type: text/text ;Charset=" + charset + "\n").getBytes(charset));
            out.write(("Content-Length: " + message.length() + "\n").getBytes(charset));
            out.write("\n".getBytes());
            out.write(message.getBytes());
        }catch(IOException ex){
            ex.printStackTrace(System.out);
        }
    }

    /**
     * Sets the content type for this response.
     * Automatically set by according to @POST or @GET response value.
     * @param contentType String content type.
     */
    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Sets the char set for this response.
     * @param charset Charset
     */
    @Override
    public void setCharset(Charset charset){
        this.charset=charset;
    }

    /**
     * Sets the POST data for this response if viable.
     * @param data byte[] data.
     */
    @Override
    public void setData(byte[] data) {
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
