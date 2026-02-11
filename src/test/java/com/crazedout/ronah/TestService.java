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

import com.crazedout.ronah.annotation.GET;
import com.crazedout.ronah.annotation.PATH;
import com.crazedout.ronah.annotation.POST;
import com.crazedout.ronah.service.AutoRegisterService;
import com.crazedout.ronah.service.HttpRequest;
import com.crazedout.ronah.service.Request;

@SuppressWarnings("unused")
@PATH(path="/test")
public class TestService extends AutoRegisterService {

    public TestService(){
        super();
    }

    @GET(path="/", response="text/html", ignoreParentPath = true)
    public void getIndex(Request request){
        String html = "<!DOCTYPE html><html><body><h1>Hello from Ronah</h1></body></html>";
        request.getResponse().ok(html).send();
    }

    @GET(path="/api/v1")
    public void getRest(Request request){
        String json = "{\"name\":\"Ronah\",\"value\":\"server\"}";
        request.getResponse().ok(json).send();
    }

    @GET(path="/api/v1", params={"name","value"})
    public void getRest1(Request request, String name, String value){
        String json = String.format("{\"name\":\"%s\",\"value\":\"%s\"}",name,value);
        request.getResponse().ok(json).send();
    }

    @POST(path="/api/v1/post", params={"name","value"}, acceptContentType = HttpRequest.APPLICATION_JSON)
    public void getData3(Request request, String name, String value){
        System.out.println(name + " " + value);
        String json = new String(request.getPostData());
        request.getResponse().ok(json).send();
    }

    @POST(path="/api/v1/post", acceptContentType = HttpRequest.X_WWW_FORM_URLENCODED)
    public void getData1(Request request){
        System.out.println("Here!");
        String json = new String(request.getPostData());
        request.getResponse().ok(json).send();
    }


}
