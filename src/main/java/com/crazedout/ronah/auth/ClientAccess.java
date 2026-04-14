package com.crazedout.ronah.auth;

import com.crazedout.ronah.util.WildcardMatcher;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ClientAccess {

    private static ClientAccess instance;

    private final List<String> clients;
    private final List<String> cached;

    private final static Logger logger = Logger.getLogger(ClientAccess.class.getName());

    private boolean allowAll = false;

    private ClientAccess(){
        clients = new ArrayList<>();
        cached = new ArrayList<>();
        clients.add("127.0.0.1");
        clients.add("0:0:0*");
        String prop = System.getProperty("ronah.clients.allowed");
        if(prop!=null) {
            allowAll=false;
            String[] tok = prop.split(",");
            for (String t : tok) {
                clients.add(t.trim());
            }
        }
    }

    public static void addClient(String ip){
        getInstance().clients.add(ip);
    }

    public static void removeClient(String ip){
        getInstance().clients.remove(ip);
    }

    public static void allowAll(boolean allow){
        getInstance().allowAll=allow;
    }

    public static boolean allowed(Socket s){

        if(getInstance().allowAll) return true;

        String ip = s.getInetAddress().toString();
        if(ip.startsWith("/")) ip=ip.substring(1);
        for(String c: getInstance().clients){
            System.out.println(c + " " + ip);
            if(WildcardMatcher.matches(ip,c)) {
                if(!getInstance().cached.contains(ip)) {
                    getInstance().cached.add(ip);
                    logger.info("Accepting:" + ip);
                }
                return true;
            }
        }
        logger.info("Ronah Deny:" + ip.substring(1));
        return false;
    }

    private static ClientAccess getInstance(){
        if(instance==null){
            instance = new ClientAccess();
        }
        return instance;
    }

}
