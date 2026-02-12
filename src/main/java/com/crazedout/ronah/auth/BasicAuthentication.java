package com.crazedout.ronah.auth;

import com.crazedout.ronah.service.Request;
import com.crazedout.ronah.service.User;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class BasicAuthentication {

    private static BasicAuthentication instance;
    private final List<BasicUser> users;

    public static class BasicUser implements User {

        String passwd, user, enc;

        BasicUser(String user, String passwd) {
            this.user = user;
            this.passwd = passwd;
            enc = new String(Base64.getEncoder().encode((user + ":" + passwd).getBytes()));
        }

        @Override
        public String getUser(){
            return this.user;
        }

        public String getEncoded(){
            return this.enc;
        }

    }

    private BasicAuthentication(){
        this.users = new ArrayList<>();
    }

    public static void addUser(String user, String passwd){
        getInstance().users.add(new BasicUser(user,passwd));
    }

    public static BasicUser authenticate(Request request){

        String auth = request.getHeader("Authorization");
        if(auth==null) return null;
        if(auth.startsWith("Basic")){
            String[] split = auth.split(" ");
            for(BasicUser user:getInstance().users){
                if(user.enc.equals(split[1])){
                    return user;
                }
            }
        }
        return null;
    }

    private static BasicAuthentication getInstance(){
        if(instance==null){
            instance = new BasicAuthentication();
        }
        return instance;
    }

}
