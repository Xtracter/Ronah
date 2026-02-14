package com.crazedout.ronah.auth;

import com.crazedout.ronah.request.Request;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Handling of HTTP Basic Authentication.
 * <b><WARNING: Not safe unless Secure Sockets is used.</b>
 */
@SuppressWarnings("unused")
public class BasicAuthentication {

    private static BasicAuthentication instance;
    private final List<BasicUser> users;

    /**
     * Implementation of User.
     */
    public static class BasicUser implements User {

        String passwd, user, enc;

        BasicUser(String user, String passwd) {
            this.user = user;
            this.passwd = passwd;
            enc = new String(Base64.getEncoder().encode((user + ":" + passwd).getBytes()));
        }

        @Override
        public String getUserName(){
            return this.user;
        }

        @SuppressWarnings("unused")
        public String getEncoded(){
            return this.enc;
        }

    }

    private BasicAuthentication(){
        this.users = new ArrayList<>();
    }

    /**
     * Adds a valid User to this Basic Authentication.
     * @param user String user name
     * @param passwd String password
     */
    public static void addUser(String user, String passwd){
        getInstance().users.add(new BasicUser(user,passwd));
    }

    /**
     * Authenticates with this Requests 'Authorization' header,
     * @param request Request incoming request
     * @return User if validated else null.
     */
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
