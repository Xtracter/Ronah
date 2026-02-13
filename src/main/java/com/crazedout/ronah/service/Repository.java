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
import com.crazedout.ronah.annotation.*;
import com.crazedout.ronah.auth.BasicAuthentication;
import com.crazedout.ronah.service.handler.MultipartPart;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * Class to handle Services and dispatch requests to correct Service,
 * @param <Service>
 */

@SuppressWarnings("unused")
public final class Repository<Service> extends ArrayList<Service> {

    private final static Logger logger = Ronah.logger;
    private static Repository<com.crazedout.ronah.service.Service> instance;

    private Repository(){}

    private static Repository<com.crazedout.ronah.service.Service> getInstance(){
        if(instance==null) instance = new Repository<>();
        return instance;
    }

    public static List<com.crazedout.ronah.service.Service> getServices(){
        return getInstance();
    }

    /**
     * This central function dispatches incoming calls to the correct Service's method.
     * @param request Request request.
     */
    static void serv(Request request) {
        String errMess;
        boolean sent = false;
        String parentPath = "";
        Method catchAll=null;
        com.crazedout.ronah.service.Service catchService=null;
        for(com.crazedout.ronah.service.Service s : getInstance()){
            if(s.getClass().getAnnotations().length>0){
                PATH path = (PATH)s.getClass().getAnnotations()[0];
                parentPath = path.path();
                if(parentPath.endsWith("/")) parentPath = path.path().substring(0,path.path().length()-1);
            }
            Method[] methods = s.getClass().getMethods();
            for(Method m:methods) {
                try {
                    if(m.getAnnotationsByType(CatchAll.class).length>0){
                        catchAll = m;
                        catchService = s;
                        logger.info("Catch all: " + m.getName());
                    }
                    if(parseMethods(s, request, m, parentPath)) {
                        sent = true;
                    }
                }catch(IllegalAccessException|InvocationTargetException ex){
                    ex.printStackTrace(System.out);
                }
            }
        }
        if(!sent){
            try {
                if (catchAll != null) catchAll.invoke(catchService, request);
            }catch(IllegalAccessException | InvocationTargetException ex){
                ex.printStackTrace(System.out);
                request.getResponse().notFound().send();
            }
            request.getResponse().notFound().send();
        }
    }

    /**
     * Parses methods an invokes.
     * @param s Service
     * @param request Request
     * @param method Http Method
     * @param parentPath String request path
     * @return boolean invoked or not.
     * @throws InvocationTargetException Exception
     * @throws IllegalAccessException Exception
     */
    static boolean parseMethods( com.crazedout.ronah.service.Service s, Request request, Method method, String parentPath)
    throws InvocationTargetException, IllegalAccessException {

        boolean sent=false;

         for(Annotation an: method.getDeclaredAnnotations()) {
            // GET request
            if((an instanceof GET g) && Repository.pathEquals(request, g.path(), parentPath,g.ignoreParentPath())) {
                if(g.useBasicAuth()){
                    User user;
                    if((user= BasicAuthentication.authenticate(request))==null){
                        request.getResponse().auth(g.basicAuthRealm()).send();
                        return true;
                    }
                }
                request.getResponse().setContentType(g.response());
                Parameter[] params = method.getParameters();
                List<Object> args = new ArrayList<>();
                args.add(request);
                for (Parameter p : params) {
                    if (p.getAnnotationsByType(Param.class).length > 0) {
                        String value = request.getParameter(p.getName().toLowerCase());
                        if (value != null){
                            addParameterByClass(args,value,p.getType());
                        }
                    }
                }
                if(!args.isEmpty() && method.getParameterCount()>1) {
                    logger.info("Invoking method: " + method.getName());
                    method.invoke(s, args.toArray());
                    sent=true;
                }else{
                    logger.info("Invoking method: " + method.getName());
                    method.invoke(s,request);
                    sent=true;
                }
                break;
            }else if((an instanceof POST p) && Repository.pathEquals(request, p.path(), parentPath,
                    p.ignoreParentPath())){
                if(p.useBasicAuth()){
                    User user;
                    if((user=BasicAuthentication.authenticate(request))==null){
                        request.getResponse().auth(p.basicAuthRealm()).send();
                        return true;
                    }
                }
                request.getResponse().setContentType(p.response());
                Parameter[] params = method.getParameters();
                List<Object> args = new ArrayList<>();
                args.add(request);
                for (Parameter pa : params) {
                    if (pa.getAnnotationsByType(Param.class).length > 0) {
                        if(HttpRequest.X_WWW_FORM_URLENCODED.equals(request.getHeader("Content-Type"))) {
                            String value = request.getParameter(pa.getName().toLowerCase());
                            if(value!=null){
                                addParameterByClass(args, value, pa.getType());
                            }
                        }else if(HttpRequest.APPLICATION_JSON.equals(request.getHeader("Content-Type"))) {
                            String value = new String(request.getPostData());
                            addParameterByClass(args, value, pa.getType());
                        }else if(request.getHeader("Content-Type").startsWith(HttpRequest.MULTIPART_FORM_DATA)) {
                            String value;
                            System.out.println("Multi:" + request.getMultiParts().size());
                        }
                    }
                }
                System.out.println(args.size() + " " + method.getParameterCount());
                //if(args.size()==method.getParameterCount()) {
                    logger.info("Invoking method: " + method.getName());
                    method.invoke(s, args.toArray());
                    sent=true;
                //}
                break;
            }
         }
        return sent;
    }

    /**
     * Sets the correct tpe of an parameter.
     * @param args List paramers args
     * @param value String value as string
     * @param type Class the type of parameter to be set.
     */
    private static void addParameterByClass(List<Object> args, String value, Class<?> type){
        if(value!=null) {
            if (type == Integer.class) args.add(Integer.parseInt(value));
            else if (type == Double.class) args.add(Double.parseDouble(value));
            else if (type == Float.class) args.add(Float.parseFloat(value));
            else if (type == Long.class) args.add(Long.parseLong(value));
            else if (type == JSONObject.class) {args.add(new JSONObject(value));
            } else args.add(value);
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
     * @param path String path
     * @return boolean true/false.
     */
    private static boolean pathEquals(Request request, String path, String parentPath, boolean ignoreParentPath){

        String str1 = request.getPath();
        String str2 = path;

        if(!ignoreParentPath) str2 = parentPath + str2;
        if(str1.length()>1 && str1.charAt(str1.length()-1)!='/') str1+="/";
        if(str2.length()>1 && str2.charAt(str2.length()-1)!='/') str2+="/";

        return str1.equals(str2);
    }

    /**
     * Gets the number of Servies registered.
     * @return int size.
     */
    public static int getSize(){
        return getInstance().size();
    }

    /**
     * Add Service. Only one of a kind can be registered.
     * @param service Service
     */
    public static void addService(com.crazedout.ronah.service.Service service){
        if(!getInstance().contains(service)) {
            getInstance().add(service);
        }
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
