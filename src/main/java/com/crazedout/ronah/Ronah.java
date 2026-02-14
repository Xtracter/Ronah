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

import javax.net.ssl.SSLServerSocketFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.logging.Logger;

public final class Ronah {

    private final static Logger logger = Logger.getLogger(Ronah.class.getName());

    /**
     * Gets nice program Marquee.
     * @return String marquee.
     */
    private static String marquee(){

        return
                """
                        *******************************
                        *     RONAH REST SERVER 1.0   *
                        *       c8Soft 2026 (c)       *
                        *******************************
                        """;
    }

    private static void usage(){
        System.out.println("Usage:");
        System.out.println("-p:<port> (optional default 8080)");
        System.out.println("-s:<service> comma separated list of Services.");
        System.out.println("-Dronah.services=<services> comma separated list of Services.");
        System.out.println("-Dronah.port=<port> (optional default 8080)");
        System.out.println("-Djavax.net.ssl.keyStore=<keystore.jks>");
        System.out.println("-Djavax.net.ssl.keyStorePassword=<password>");

    }

    private static void addServices(String serv) throws
            ClassNotFoundException,
            NoSuchMethodException,
            InvocationTargetException,
            InstantiationException,
            IllegalAccessException {

        if (serv.contains(",")) {
            Arrays.stream(serv.split(",")).forEach(e -> {
                try {
                    Class.forName(e).getDeclaredConstructor().newInstance();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        } else Class.forName(serv).getDeclaredConstructor().newInstance();
    }

    /**
     * Application start point.
     * @param args command line arguments (i.e. port).
     */
    public static void main(String[] args)  throws ClassNotFoundException,
        NoSuchMethodException,
        InvocationTargetException,
        InstantiationException,
        IllegalAccessException {

        System.out.print(marquee());
        int port = 8080;
        for(String s:args){
            if(s.startsWith("-p:")) port = Integer.parseInt(s.substring(3));
            else if(s.equals("-help")) {
                usage();
                return;
            }else if(s.startsWith("-s:")){
                String serv = s.substring(3);
                addServices(serv);
            }
        }
        new APIService();

        String vm = System.getProperty("ronah.services");
        if(vm!=null) addServices(vm);
        vm = System.getProperty("ronah.port");
        if(vm!=null) port = Integer.parseInt(vm);

        RonahHttpServer r = new RonahHttpServer();
        if (System.getProperty("javax.net.ssl.keyStore") != null) {
            r.setServerSocketFactory(SSLServerSocketFactory.getDefault());
            logger.info("TLS enabled.");
        }
        r.start(port);
    }

}
