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

import com.crazedout.ronah.annotation.*;
import com.crazedout.ronah.request.Request;
import com.crazedout.ronah.handler.Repository;

@SuppressWarnings("unused")
public class DefaultService extends AutoRegisterService{

    public DefaultService(){
        super();
    }

    @CatchAll
    public void catchAll(Request request){
        String html = "<!DOCTYPE html><html><body><h1>Hello from Ronah Catch all</h1>Register services: "+ Repository.getSize()+"</body></html>";
        request.getResponse().ok(html).send();
    }
}
