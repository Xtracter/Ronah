package com.crazedout.ronah;

import com.crazedout.ronah.api.APIService;

import javax.net.ssl.SSLServerSocketFactory;
import java.util.logging.Logger;

public class Ronah {

    private static Logger logger = Logger.getLogger(Ronah.class.getName());

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
    public static void main(String[] args) {

        System.out.println(marquee());
        new MyRESTService();
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
