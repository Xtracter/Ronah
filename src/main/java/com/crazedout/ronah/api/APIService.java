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
import com.crazedout.ronah.service.AutoRegisterService;
import com.crazedout.ronah.service.DefaultService;
import com.crazedout.ronah.service.Request;

public class APIService extends AutoRegisterService {

    @GET(path="/baggins", response="text/html")
    public void getBagins(Request request){

        String html = APIFactory.getHTML(DefaultService.class);
        request.getResponse().ok(html).send();
    }

}
