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

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * This class acts as parser for incoming HTTP calls.
 * Creates HttpRequest object accordingly to the client request.
 * Calls the Repository for the correct Service to handle this Request.
 */
public final class HttpHandler {

    private final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * Constructor
     * @param s Client socket.
     */
    public HttpHandler(Socket s) {
        try {
            parseRequest(s.getInputStream(), s.getOutputStream());
            s.close();
        }catch(Exception ex){
            logger.warning(ex.getMessage());
            if(Ronah.verbose) ex.printStackTrace(System.err);
        }
    }

    /**
     * Parse HTTP request.
     * @param in Socket Inputstream
     * @param out Sockets OutputStream
     * @throws IOException Exception
     * @throws IllegalAccessException Exception
     * @throws InvocationTargetException Exception
     */
    private void parseRequest(InputStream in, OutputStream out) throws IOException, InvocationTargetException,
            IllegalAccessException {

        HttpRequest request=null;
        StringBuilder line = new StringBuilder();

        int r;
        while((r=in.read())>-1){
            char c = (char)r;
            if(Ronah.verbose) System.out.print(c);
            if(c=='\r') continue;
            if(c=='\n'){
                if(request==null) {
                    request = new HttpRequest(line.toString(),out);
                }
                if(line.isEmpty()) break;
                if(line.toString().contains(":")){
                    String[] tokens = line.toString().split(":");
                    request.getHeaders().put(tokens[0],tokens[1].trim());
                }
                if(Ronah.verbose) logger.info(line.toString());
                line = new StringBuilder();
            }else {
                line.append(c);
            }
        }

        if(request != null && ("POST".equals(request.getMethod())) &&
                request.getHeaders().get("Content-Length")!=null) {
            int len = Integer.parseInt(request.getHeaders().get("Content-Length"));
            line = new StringBuilder();
            for (int i = 0; i < len; i++) {
                line.append((char) in.read());
            }
            if (Ronah.verbose) logger.info("POST data read:" + len + " bytes");
            request.setPostData(line.toString().getBytes());
        }
        Repository.serv(request);
    }
}
