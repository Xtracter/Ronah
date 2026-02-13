package com.crazedout.ronah.api;
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
import com.crazedout.ronah.service.HttpRequest;
import com.crazedout.ronah.service.Service;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@SuppressWarnings("all")
public class APIFactory {

    private static APIFactory instance;

    private APIFactory(){
    }

    public static String getHTML(Class s){
        return getInstance().parse(s);
    }

    private static APIFactory getInstance(){
        if(instance==null) instance = new APIFactory();
        return instance;
    }

    public String parse(Class<Service> service){
        StringBuilder sb = new StringBuilder();
        for(Method method:service.getDeclaredMethods()){
            API bagins = method.getAnnotation(API.class);
            GET g = method.getAnnotation(GET.class);
            POST p = method.getAnnotation(POST.class);
            if(g!=null && bagins!=null) {
                sb.append(getHTML(method.getParameters(), bagins.name(), "GET", g.acceptContentType(), g.path(), g.response()));
            }else if(p!=null && bagins!=null){
                sb.append(getHTML(method.getParameters(), bagins.name(), "POST", p.acceptContentType(), p.path(), p.response()));
            }
        }
        return sb.toString();
    }


    String parseType(Class<?> c){
        return c.toString().substring(c.toString().lastIndexOf(".")+1);
    }

    private int count=0;
    String getHTML(Parameter[] params, String name, String method, String contentType, String path, String response){

        String uuid = "bagins" + UUID.randomUUID().toString().replace("-","_");
        List<String> keys = new ArrayList<>();
        List<String> names = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        sb.append("<table>\n");
        sb.append("<tr><th>Name</th><th>Method</th><th>Path</th><th>"+contentType+"</th><th>Response</th></tr>\n");
        sb.append("<tr valign=\"top\"><td>").append(name).append("</td><td>").append(method).append("</td><td>").append(path).append("</td>\n");
        sb.append("<td>\n");

        int c=0;
        String func;
        for(Parameter p:params){
            if(c++==0) continue;
            String key = "p_" + count++;
            String sid = "sp_"+ count++;
            if(HttpRequest.APPLICATION_JSON.equals(contentType)) {
                sb.append(String.format("%s (%s):<br/> <input type='text' name='%s' id='%s' onBlur=\"isJsonString(this.value,'%s')\"/><span style=\"color:red\" id=\"%s\"></span><br/>\n",
                        p.getName(), parseType(p.getType()), p.getName().toLowerCase(), key,sid,sid));
            }else{
                if(isNumber(p.getType())) {
                    sb.append(String.format("%s (%s):<br/> <input type='text' name='%s' id='%s' onBlur=\"if(isNaN(this.value)) {document.getElementById('%s').innerHTML='Not a Number'}else{document.getElementById('%s').innerHTML=''}\"/><span style=\"color:red\" id=\"%s\"></span><br/>\n",
                            p.getName(), parseType(p.getType()), p.getName().toLowerCase(), key,sid,sid,sid));
                }else{
                    sb.append(String.format("%s (%s):<br/> <input type='text' name='%s' id='%s' /><br/>\n",
                            p.getName(), parseType(p.getType()), p.getName().toLowerCase(), key));
                }
            }

            keys.add(key);
            names.add(p.getName().toLowerCase());
        }
        if("POST".equals(method)) {
            func = "pAjax('con_" + uuid + "','" + path + "',";
        }else{
            func = "gAjax('con_" + uuid + "','" + path + "',";
        }
        if(params.length<2){
            String key = "q_" + count++;
            sb.append(String.format("Query String:<br/><input type='text' name='%s' id='%s' /><br/>\n", key, key));
            keys.add(key);
            names.add("Query:");
            func = "qAjax" + "('con_" + uuid + "','" + path + "',";
        }
        for (String key : keys) {
            func += "'" + key + "',";
        }
        func=func.substring(0,func.length()-1) + ")";
        sb.append("</td><td>").append(response).append("</td></tr>");
        String btn = "<input type='button' value='Send' onclick=\"" + func + "\"/>\n";
        sb.append(String.format("<tr><td colspan=3>%s</td></tr>", btn));
        String console = String.format("<div style=\"border:1px solid gray; height: 100px; background: #d1d1d1\" id='%s'>Response:</div>", "con_" + uuid);
        sb.append(String.format("<tr><td colspan=3>%s</td></tr>", console));

        sb.append("</table><br/>\n");


        return sb.toString();
    }

    boolean isNumber(Class<?> c){
        return c==Integer.class || c==Double.class || c == Float.class;
    }

    public static String getHead(Class<?> c){
        String html = "Fail";
        try(DataInputStream dis = new DataInputStream(Objects.requireNonNull(
                c.getResourceAsStream("/api_head.html")))){
            html = new String(dis.readAllBytes());
        }catch(IOException ex){
            ex.printStackTrace(System.out);
        }
        return html;
    }

    public static String getTail(Class<?> c){
        String html = "Fail";
        try(DataInputStream dis = new DataInputStream(Objects.requireNonNull(
                c.getResourceAsStream("/api_tail.html")))){
            html = new String(dis.readAllBytes());
        }catch(IOException ex){
            ex.printStackTrace(System.out);
        }
        return html;
    }
}
