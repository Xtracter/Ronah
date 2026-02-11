package com.crazedout.ronah.bagins;
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
import com.crazedout.ronah.service.Service;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class BaginsFactory {

    private static BaginsFactory instance;
    int n = 0;

    private BaginsFactory(){
    }

    public static String getHTML(Class s){
        return getInstance().parse(s);
    }

    private static BaginsFactory getInstance(){
        if(instance==null) instance = new BaginsFactory();
        return instance;
    }

    public String parse(Class<Service> service){
        StringBuilder sb = new StringBuilder();
        for(Method method:service.getDeclaredMethods()){
            Bagins bagins = method.getAnnotation(Bagins.class);
            GET g = method.getAnnotation(GET.class);
            POST p = method.getAnnotation(POST.class);
            System.out.println("jan:" + g + " " + bagins);
            if(g!=null && bagins!=null) {
                sb.append(getHTML(method.getParameters(), bagins.name(), "GET", g.path(), g.response()));
            }else if(p!=null && bagins!=null){
                sb.append(getHTML(method.getParameters(), bagins.name(), "POST", p.path(), p.response()));
            }
        }
        return getHead() + sb.toString() + getTail();
    }

    private int count=0;
    String getHTML(Parameter[] params, String name, String method, String path, String response){

        String uuid = "bagins" + UUID.randomUUID().toString().replace("-","_");
        List<String> keys = new ArrayList<>();
        List<String> names = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        sb.append("<table>\n");
        sb.append("<tr><th>Name</th><th>Method</th><th>Path</th><th>Query</th><th>Response</th></tr>\n");
        sb.append("<tr valign=\"top\"><td>"+name+"</td><td>"+method+"</td><td>"+path+"</td>\n");
        sb.append("<td>\n");

        int c=0;
        String func="";
        for(Parameter p:params){
            if(c++==0) continue;
            String key = "p_" + count++;
            sb.append(String.format("<input type='text' name='%s' id='%s' />\n", p.getName().toLowerCase(),key));
            keys.add(key);
            names.add(p.getName().toLowerCase());
            func += uuid + "('" + path + "',";
        }
        if(params.length<2){
            String key = "q_" + count++;
            sb.append(String.format("<input type='text' name='%s' id='%s' />\n", key, key));
            keys.add(key);
            names.add("Query:");
            func += "q" + uuid + "('" + path + "',";
        }
        for(int i = 0; i < keys.size(); i++){
            func+="'" + keys.get(i) + "',";
        }
        func=func.substring(0,func.length()-1) + ")";
        sb.append("</td><td>").append(response).append("</td></tr>");
        String btn = "<input type='button' value='Send' onclick=\""+func+"\"/>\n";
        sb.append(String.format("<tr><td colspan=3>%s</td></tr>", btn));
        sb.append("</table><br/>\n");


        return sb.toString();
    }


    String getHead(){
        String html = "Fail";
        try(DataInputStream dis = new DataInputStream(Objects.requireNonNull(
                getClass().getResourceAsStream("/bagins_head.html")))){
            html = new String(dis.readAllBytes());
        }catch(IOException ex){
            ex.printStackTrace(System.out);
        }
        return html;
    }

    String getTail(){
        String html = "Fail";
        try(DataInputStream dis = new DataInputStream(Objects.requireNonNull(
                getClass().getResourceAsStream("/bagins_tail.html")))){
            html = new String(dis.readAllBytes());
        }catch(IOException ex){
            ex.printStackTrace(System.out);
        }
        return html;
    }
}
