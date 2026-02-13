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

import com.crazedout.ronah.service.handler.RawMultipartParser;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * This class acts as parser for incoming HTTP calls.
 * Creates HttpRequest object accordingly to the client request.
 * Calls the Repository for the correct Service to handle this Request.
 */
public final class HttpHandler {

    /**
     * Constructor
     * @param s Client socket.
     */
    public HttpHandler(Socket s) {
        try {
            parseRequest(s.getInputStream(), s.getOutputStream(), s.getRemoteSocketAddress());
            s.close();
        }catch(Exception ex){
            RonahHttpServer.logger.warning(ex.getMessage());
            if(RonahHttpServer.verbose) ex.printStackTrace(System.err);
        }
    }

    /**
     * Parse HTTP request.
     * @param in Socket Inputstream
     * @param out Sockets OutputStream
     * @throws IOException Exception
     */
    private void parseRequest(InputStream in, OutputStream out, SocketAddress sockAddr) throws IOException {

        HttpRequest request=null;
        StringBuilder line = new StringBuilder();

        int r;
        while((r=in.read())>-1){
            char c = (char)r;
            if(System.getProperty("ronah.debug.http")!=null) {
                System.out.print(c);
            }
            if(c=='\r') continue;
            if(c=='\n'){
                if(request==null) {
                    RonahHttpServer.logger.info("Remote:" + sockAddr + " " + line);
                    request = new HttpRequest(line.toString(),in,out);
                }
                if(line.isEmpty()) break;
                if(line.toString().contains(":")){
                    String[] tokens = line.toString().split(":");
                    request.getHeaders().put(tokens[0],tokens[1].trim());
                }
                line = new StringBuilder();
            }else {
                line.append(c);
            }
        }

        if(request != null && ("POST".equals(request.getMethod())) &&
                request.getHeaders().get("Content-Length")!=null) {
            int len = Integer.parseInt(request.getHeader("Content-Length"));
            byte[] buffer = new byte[len];

            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) in.read();
            }
            request.setPostData(buffer);
            if(request.getHeader("Content-Type").startsWith(HttpRequest.MULTIPART_FORM_DATA)){

                    request.setMultiParts(RawMultipartParser.parse(buffer, request.getHeader("Content-Type"),
                            StandardCharsets.UTF_8));

            }
            if(HttpRequest.X_WWW_FORM_URLENCODED.equals(request.getHeader("Content-Type"))) {
                request.setQueryString(new String(buffer));
            }
        }
        try {
            if(request!=null){
                Repository.serv(request);
            }
        }catch(Exception ex){
            ex.printStackTrace(System.out);
            request.getResponse().error(ex.getMessage()).send();
        }
    }
}



















