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
import com.crazedout.ronah.service.DefaultService;
import com.crazedout.ronah.service.Repository;
import com.crazedout.ronah.service.RonahHttpServer;
import com.crazedout.ronah.service.Service;

import javax.net.ssl.SSLServerSocketFactory;
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

    /**
     * Application start point.
     * @param args command line arguments (i.e. port).
     */
    public static void main(String[] args) throws Exception {

        System.out.println(marquee());
        /*
            VM Option: -Dronah.service.class=org.user.ronah.MyService
         */
        String service = System.getProperty("ronah.service.class");
        if(service==null) new DefaultService();
        else Class.forName(service).getDeclaredConstructor().newInstance();
        new APIService();

        int port = 0;
        if(args.length>0){
            try{
                port = Integer.parseInt(args[0]);
            }catch(NumberFormatException ex){
                try {
                    port = System.getProperty("ronah.port") != null ? Integer.parseInt(System.getProperty("ronah.port")) : 8080;
                }catch(NumberFormatException e){
                    port = 8080;
                    logger.info(String.format("Bad VM Option -Dronah.port=%s Defaulting to port: 8080",
                            System.getProperty("ronah.port")));
                }
            }
        }
        if(port==0) {
            port = 8080;
            logger.info("Port not set - defaulting to 8080.");
        }
        RonahHttpServer r = new RonahHttpServer();
        if (System.getProperty("javax.net.ssl.keyStore") != null) {
            r.setServerSocketFactory(SSLServerSocketFactory.getDefault());
            logger.info("TLS enabled.");
        }
        r.start(port);
    }

}
