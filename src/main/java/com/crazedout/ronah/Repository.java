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

import com.crazedout.ronah.annotation.*;
import com.crazedout.ronah.auth.User;
import com.crazedout.ronah.request.multipart.MultipartPart;
import com.crazedout.ronah.request.ContentType;
import com.crazedout.ronah.request.HttpRequest;
import com.crazedout.ronah.service.Service;
import com.crazedout.ronah.util.WildcardMatcher;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import static com.crazedout.ronah.auth.BasicAuthentication.authenticate;


/**
 * Class to handle Services and dispatch requests to correct Service,
 */

@SuppressWarnings("unused")
public final class Repository extends ArrayList<com.crazedout.ronah.service.Service> {

    private final static Logger logger = RonahHttpServer.logger;
    private static Repository instance;

    private Repository(){}

    public static Repository getInstance(){
        if(instance==null) instance = new Repository();
        return instance;
    }

    public static List<com.crazedout.ronah.service.Service> getServices(){
        return getInstance();
    }

    /**
     * This central function dispatches incoming calls to the correct Service's method.
     * @param request Request request.
     */
    void serv(HttpRequest request) {
        String errMess;
        boolean sent = false;
        String parentPath = "";
        Method catchAll=null;
        com.crazedout.ronah.service.Service catchService=null;
        for(com.crazedout.ronah.service.Service s : getInstance()){
            if(!s.isActive()) continue;
            if(s.getClass().getAnnotations().length>0){
                Parent parent = (Parent)s.getClass().getAnnotations()[0];
                parentPath = parent.path();
                if(parentPath.endsWith("/")) parentPath = parent.path().substring(0,parent.path().length()-1);
                if(!allowClientIP(request.getSocketAddress(),parent)) {
                    request.getResponse().forbidden().send();
                    return;
                }
                if(request.getHeader("Origin")!=null && parent.allowCORSOrigins().length>0){
                    request.getResponse().applyCORSHeaders(request,parent.allowCORSOrigins());
                }
            }
            Method[] methods = s.getClass().getMethods();
            Arrays.sort(methods, Comparator.comparing(Method::getName));
            for(Method m:methods) {
                try {
                    if(m.getAnnotationsByType(CatchAll.class).length>0){
                        catchAll = m;
                        catchService = s;
                        logger.info("Catch all: " + s.getClass().getName() + " " + m.getName());
                    }
                    if(parseMethods(s, request, m, parentPath)) {
                        sent = true;
                    }
                }catch(IllegalAccessException|InvocationTargetException ex){
                    ex.printStackTrace(System.out);
                    String err = printToString(ex);
                    if("application/json".equals(request.getHeader("Content-Type"))){
                        err+="\napplication/json method should only have HttpRequest and JSONObject as parameters.";
                    }
                    request.getResponse().error(err).send();
                    sent=true;
                }
            }
        }
        if(!sent){
            try {
                if (catchAll != null) catchAll.invoke(catchService, request);
                else request.getResponse().notFound().send();
            }catch(IllegalAccessException | InvocationTargetException ex){
                ex.printStackTrace(System.out);
            }
        }
    }

    boolean allowClientIP(InetSocketAddress sockAddr, Parent p){
        if(p.allowClientIP().length==0) return true;
        for(String s:p.allowClientIP()){
            String[] split = sockAddr.toString().split(":");
            if(WildcardMatcher.matches(split[0].substring(1),s)) return true;
        }
        return false;
    }

    String printToString(Exception ex)  {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final String utf8 = StandardCharsets.UTF_8.name();
        try (PrintStream ps = new PrintStream(baos, true)) {
            ex.printStackTrace(ps);
        }
        return baos.toString();
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
    boolean parseMethods( com.crazedout.ronah.service.Service s, HttpRequest request, Method method, String parentPath)
    throws InvocationTargetException, IllegalAccessException {
        boolean sent=false;
         for(Annotation an: method.getDeclaredAnnotations()) {
             if((an instanceof OPTIONS o) && pathEquals(request, o.path(), parentPath, o.ignoreParentPath())) {
                 handleOptions(s,request,o,method);
                 sent=true;
             }else if((an instanceof GET g) && pathEquals(request, g.path(), parentPath,g.ignoreParentPath())) {
                 handleGET(s,request,g,method);
                 sent = true;
             }else if((an instanceof POST p) && pathEquals(request, p.path(), parentPath,
                     p.ignoreParentPath())) {
                 handlePOST(s,request,p,method);
                 sent=true;
             }
             if(sent) break;
         }
         // TODO: Really bad pattern with boolean return here. Fix it soon.
        return sent;
    }

    void handlePOST(com.crazedout.ronah.service.Service s,HttpRequest request,POST p, Method method)
            throws InvocationTargetException, IllegalAccessException{

        if(p.useBasicAuth()){
            User user;
            if(authenticate(request)==null){
                request.getResponse().auth(p.basicAuthRealm()).send();
                return;
            }
        }
        request.getResponse().setContentType(p.response());
        Parameter[] params = method.getParameters();
        List<Object> args = new ArrayList<>();
        args.add(request);
        for (Parameter pa : params) {
            if (pa.getAnnotationsByType(Param.class).length > 0) {
                if(ContentType.APPLICATION_X_WWW_FORM_URLENCODED.equals(request.getHeader("Content-Type"))) {
                    String value = request.getParameter(pa.getName().toLowerCase());
                    if(value!=null){
                        addParameterByClass(args, value, pa.getType());
                    }else if(p.enforceParams()){
                        args.add(null);
                    }
                }else if(ContentType.APPLICATION_JSON.equals(request.getHeader("Content-Type"))) {
                    String value = new String(request.getPostData());
                    JSONObject jsonObject = getJSONObject(args);
                    if(jsonObject==null) {
                        addParameterByClass(args, value, pa.getType());
                    }else{
                        try {
                            String val = jsonObject.getString(pa.getName());
                            addParameterByClass(args, val, pa.getType());
                        }catch(JSONException ex){
                            if(!p.enforceParams()) {
                                args.add(null);
                            }
                        }
                    }
                }else if(request.getHeader("Content-Type")!=null &&
                        request.getHeader("Content-Type").startsWith(ContentType.MULTIPART_FORM_DATA)) {
                    for(MultipartPart part:request.getMultiParts()) {
                        if(pa.getName().equalsIgnoreCase(part.fields.get("name"))){
                            addParameterByClass(args, new String(part.body), pa.getType());
                        }else if(!p.enforceParams()){
                            args.add(null);
                        }
                    }
                }
            }
        }
        if(System.getProperty("ronah.debug")!=null){
            System.out.println(args.size() + " " + method.getParameterCount());
        }

        logger.info("Invoking method: " + method.getName());
        method.invoke(s, args.toArray());
    }

    void handleOptions(com.crazedout.ronah.service.Service s, HttpRequest request, OPTIONS o, Method method) throws
    InvocationTargetException, IllegalAccessException{
        method.invoke(s,request);
    }

    void handleGET(com.crazedout.ronah.service.Service s, HttpRequest request, GET g, Method method) throws
            InvocationTargetException, IllegalAccessException {

        if(g.useBasicAuth()){
            User user;
            if(authenticate(request)==null){
                request.getResponse().auth(g.basicAuthRealm()).send();
                return;
            }
        }
        request.getResponse().setContentType(g.response());
        Parameter[] params = method.getParameters();
        List<Object> args = new ArrayList<>();
        args.add(request);
        for (Parameter p : params) {
            if (p.getAnnotationsByType(Param.class).length > 0) {
                if(ContentType.APPLICATION_JSON.equals(request.getHeader("Content-Type"))) {
                    String value = new String(request.getPostData());
                    JSONObject jsonObject = getJSONObject(args);
                    if(jsonObject==null) {
                        addParameterByClass(args, value, p.getType());
                    }else{
                        try {
                            String val = jsonObject.getString(p.getName());
                            addParameterByClass(args, val, p.getType());
                        }catch(JSONException ex){
                            if(!g.enforceParams()) args.add(null);
                        }
                    }
                }else {
                    String value = request.getParameter(p.getName().toLowerCase());
                    if (value != null) {
                        addParameterByClass(args, value, p.getType());
                    }else if(!g.enforceParams()){
                        args.add(null);
                    }
                }
            }
        }

        logger.info("Invoking method: " + method.getName());
        method.invoke(s, args.toArray());
    }

    JSONObject getJSONObject(List<Object> args){
        for(Object o:args){
            if(o instanceof JSONObject) return (JSONObject) o;
        }
        return null;
    }

    /**
     * Sets the correct type of parameter.
     * @param args List parameters args
     * @param value String value as string
     * @param type Class the type of parameter to be set.
     */
    private void addParameterByClass(List<Object> args, String value, Class<?> type){
        if(value!=null) {
            if (type == Integer.class) args.add(Integer.parseInt(value));
            else if (type == Double.class) args.add(Double.parseDouble(value));
            else if (type == Float.class) args.add(Float.parseFloat(value));
            else if (type == Long.class) args.add(Long.parseLong(value));
            else if (type == JSONObject.class) {args.add(new JSONObject(value));} else args.add(value);
        }
    }

    /**
     * Checks if a requests content type is allowed by the Service method.
     * @param request Request request
     * @param p Annotation POST
     * @return boolean true/false
     */
    private boolean allowContentType(HttpRequest request, POST p){
        return "*".equals(p.acceptContentType()) ||
                request.getHeaders().get("Content-Type").equals(p.acceptContentType());
    }

    /**
     * Checks if the path of the request matches the annotations path.
     * @param request Request HTTP request.
     * @param path String path
     * @return boolean true/false.
     */
    private boolean pathEquals(HttpRequest request, String path, String parentPath, boolean ignoreParentPath){

        String str1 = request.getPath();
        String str2 = path;

        try {
            if (path.contains("{")) {
                parsePathParams(request, str2, str1);
                str2 = str2.substring(0, str2.indexOf("{")) + "*";
            }
        }catch(Exception ex){
            // Do Nothing;
        }


        if(!ignoreParentPath) str2 = parentPath + str2;
        if(str1.length()>1 && str1.charAt(str1.length()-1)!='/') str1+="/";
        if(str2.length()>1 && str2.charAt(str2.length()-1)!='/') str2+="/";
        return WildcardMatcher.matches(str1,str2) || (str1+"*/").equals(str2);
    }

    /**
     * Create parameters of "path" parameters denoted by /person/[name]/[age]
     *
     * @param request Request.
     * @param annotPath String path from annotation.
     * @param reqPath String path from request.
     */
    void parsePathParams(HttpRequest request,String annotPath, String reqPath){

        String[] aSplit = annotPath.split("/");
        String[] rSplit = reqPath.split("/");

        if(aSplit.length!=rSplit.length) return;

        String parsed;
        for(int i = 0; i < aSplit.length; i++){
            String word = aSplit[i];
            if(word.contains("{")){
                parsed = word.replace("{","").replace("}","");
                request.addParameter(parsed,rSplit[i].trim());
            }
        }
    }

    /**
     * Gets a Service by its path.
     * @param path String path
     * @return Service or null if not found.
     */
    public Service getServiceByPath(String path){
        for(Service s:this){
            Method[] methods = s.getClass().getMethods();
            for(Method m:methods){
                for(GET g:m.getAnnotationsByType(GET.class)){
                    if(g.path().equals(path)) return s;
                }
                for(POST p:m.getAnnotationsByType(POST.class)){
                    if(p.path().equals(path)) return s;
                }
            }
        }
        return null;
    }

    /**
     * Gets a Service by its name.
     * @param name String name
     * @return Service or null if not found.
     */
    public Service getServiceByName(String name){
        for(Service s:this){
            if(s.getName().replace(" ","_").equals(name.replace(" ","_"))) return s;
        }
        return null;
    }

    /**
     * Gets the number of Services registered.
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
