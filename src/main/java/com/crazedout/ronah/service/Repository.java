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
import com.crazedout.ronah.annotation.GET;
import com.crazedout.ronah.annotation.POST;
import com.crazedout.ronah.annotation.Path;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class to handle Services and dispatch requests to correct Service,
 * @param <Service>
 */

@SuppressWarnings("unused")
public final class Repository<Service> extends ArrayList<Service> {

    private final static Logger logger = Logger.getLogger(Repository.class.getName());
    private static Repository<com.crazedout.ronah.service.Service> instance;

    private Repository(){}

    private static Repository<com.crazedout.ronah.service.Service> getInstance(){
        if(instance==null) instance = new Repository<>();
        return instance;
    }

    /**
     * This central function dispatches incoming calls to the correct Service's method.
     * @param request Request request.
     * @throws InvocationTargetException bad Invocation.
     * @throws IllegalAccessException bad access.
     */
    static void serv(Request request) throws InvocationTargetException,
            IllegalAccessException {

        boolean sent = false;
        String parentPath = "";
        for(com.crazedout.ronah.service.Service s : getInstance()){
            if(s.getClass().getAnnotations().length>0){
                Path path = (Path)s.getClass().getAnnotations()[0];
                parentPath = path.path();
                if(parentPath.endsWith("/")) parentPath = path.path().substring(0,path.path().length()-1);
            }
            Method[] methods = s.getClass().getMethods();
            for(Method m:methods){
                Annotation[] annotations = m.getDeclaredAnnotations();
                for(Annotation a:annotations){
                    if(request.getMethod().equals("GET") && a instanceof GET g){
                        if(getInstance().pathEquals(request,g,parentPath)){
                            request.getResponse().setContentType(g.responseContentType());
                            if(m.getParameterCount()>1) {
                                parseQueryString(g,request,s,m);
                            }else {
                                m.invoke(s, request);
                            }
                            sent=true;
                        }
                    }else if(request.getMethod().equals("POST") && a instanceof POST p) {
                        if(getInstance().allowContentType(request,p) && getInstance().allowContentType(request,p)){
                            request.getResponse().setContentType(p.acceptContentType());
                            if(p.contentType().equals("application/json")) {
                                parseJson(p,request,s,m);
                            }else {
                                m.invoke(s, request);
                            }
                            sent=true;
                        }
                    }
                }
                if(sent) break;
            }
        }
        if(!sent){
            request.getResponse().notFound().send();
        }
    }

    /**
     * Parse Query String and try to match methods parameters.
     * @param annotation Current annotation
     * @param request Current Request
     * @param s Current Service
     * @param m Current Method
     */
    private static void parseQueryString(GET annotation, Request request, com.crazedout.ronah.service.Service s, Method m){
        List<Object> params = new ArrayList<>();
        params.add(request);
        byte[] bytes = request.getPostData();
        boolean handled = false;
        if(annotation.params()!=null){
            for(String param:annotation.params()) {
                Object value;
                if((value=request.getParameter(param))!=null) params.add(value);
            }
            try {
                m.invoke(s, params.toArray());
            }catch(IllegalAccessException  | InvocationTargetException ex){
                logger.warning(ex.getMessage());
            }
        }
    }

    /**
     * Parse payload and try to match methods parameters.
     * @param annotation Current annotation
     * @param request Current Request
     * @param s Current Service
     * @param m Current Method
     */
    private static void parseJson(POST annotation, Request request, com.crazedout.ronah.service.Service s, Method m){

        List<Object> params = new ArrayList<>();
        params.add(request);
        byte[] bytes = request.getPostData();
        boolean handled = false;
        if(annotation.params()!=null){
            for(String param:annotation.params()) {
                JSONObject jsonObject = new JSONObject(new String(bytes));
                Object value;
                if((value=jsonObject.get(param))!=null) params.add(value);
            }
            try {
                m.invoke(s, params.toArray());
            }catch(IllegalAccessException  | InvocationTargetException ex){
                logger.warning(ex.getMessage());
            }
        }
    }

    /**
     * Checks if a requests content type is allowed by the Service method.
     * @param request Request request
     * @param p Annotation POST
     * @return boolean true/false
     */
    private boolean allowContentType(Request request, POST p){
        return "*".equals(p.acceptContentType()) ||
                request.getHeaders().get("Content-Type").equals(p.acceptContentType());
    }

    /**
     * Checks if the path of the request matches the annotations path
     * @param request Request HTTP request.
     * @param g Annotation for Service method using GET method.
     * @return boolean true/false.
     */
    private boolean pathEquals(Request request, GET g, String parentPath){

        String str1 = request.getPath();
        String str2 = g.path();
        if(!g.ignoreParentPath()) str2 = parentPath + str2;
        if(str1.length()>1 && str1.charAt(str1.length()-1)!='/') str1+="/";
        if(str2.length()>1 && str2.charAt(str2.length()-1)!='/') str2+="/";

        return str1.equals(str2);
    }


    public static int getSize(){
        return getInstance().size();
    }

    /**
     * Add Service.
     * @param service Service
     */
    public static void addService(com.crazedout.ronah.service.Service service){
        for(com.crazedout.ronah.service.Service s:getInstance()){
            if(s.getClass()==service.getClass()){
                logger.info("Class " + s.getClass().getName() + " already in Repository. Skipping");
                return;
            }
        }
        getInstance().add(service);
    }

    /**
     * remove Service.
     * @param service Service
     */
    public static void removeService(com.crazedout.ronah.service.Service service){
        getInstance().remove(service);
    }

    /**
     * Remove all services.
     */
    public static void removeAll(){
        getInstance().clear();
    }

}
