package com.crazedout.ronah;
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

import com.crazedout.ronah.api.APIService;
import com.crazedout.ronah.service.HttpHandler;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.*;
import java.util.UUID;
import java.util.logging.*;

/**
 * Ronah REST Server
 * Version: 0.9
 * Basic HTTP REST server for easy setup and handling.
 * Date 2026-02-01
 * Author: Fredrik Roos
 * License: GPL
 * Contact: info@crazedout.com
 */

public final class RonahHttpServer {

    public static final String server = "Ronah";
    public static final String version = "1.0";
    private final String key;

    public static final Logger logger = Logger.getLogger(RonahHttpServer.class.getName());
    private boolean running=true;
    public static boolean verbose;
    private int port;
    private volatile ServerSocketFactory serverSocketFactory;
    private volatile boolean secure;
    private ServerSocket serverSocket;

    /**
     * Create an instance of Ronah on port.
     * For TLS set VM options: -Djavax.net.ssl.keyStore=<server.jks> -Djavax.net.ssl.keyStorePassword=<passwd>
     */
    public RonahHttpServer(){
        key = UUID.randomUUID().toString();
        verbose = System.getProperty("ronah.verbose") != null && "true".equals(System.getProperty("ronah.verbose"));
        if(verbose) logger.info("Verbose=true");
    }

    private ServerSocket createServerSocket(int port) throws IOException {
        ServerSocket serv = serverSocketFactory.createServerSocket();
        serv.setReuseAddress(true);
        serv.bind(new InetSocketAddress(port));
        return serv;
    }

    /**
     * Start Ronah server on port.
     * @param port TCP/IP port.
     */
    public void start(int port) {
        this.port=port;
        logger.info("Starting Ronah REST server on port:" + port);
        try{
            serverSocket = secure?createServerSocket(port):new ServerSocket(port);
            while(running) {
                Socket s = serverSocket.accept();
                s.setReuseAddress(true);
                (new Thread(() -> new HttpHandler(s))).start();
            }
            logger.info("Ronah exiting cleanly. Bye bye..");
        }catch(IOException ex){
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Stops the server
     * @throws IOException Exception
     */
    public void stop() throws IOException {
        running = false;
        serverSocket.close();  // This unblocks accept()
    }

    /**
     * Get the unique UUID of this server
     * @return String unique ket/id.
     */
    @SuppressWarnings("unused")
    public String getKey(){
        return key;
    }


    /**
     * Setting the ServerSocketFactory used to create a ServerSocket.
     * @param factory ServerSocketFactory
     */
    public void setServerSocketFactory(ServerSocketFactory factory) {
        this.serverSocketFactory = factory;
        this.secure = factory instanceof SSLServerSocketFactory;
    }

}




