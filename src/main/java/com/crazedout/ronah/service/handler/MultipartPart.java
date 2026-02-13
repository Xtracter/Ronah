package com.crazedout.ronah.service.handler;

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

import java.util.HashMap;
import java.util.Map;

public class MultipartPart {
    public Map<String, String> headers;
    public Map<String, String> fields;
    public byte[] body;

    public MultipartPart(Map<String, String> headers, byte[] body) {
        this.headers = headers;
        this.body = body;
        this.fields = new HashMap<>();
        String cp = headers.get("content-disposition");
        String[] tokens = cp.split(";");
        for(String t:tokens){
            if(t.contains("=")){
                String[] pair = t.split("=");
                fields.put(pair[0].trim(),pair[1].replace("\"","").trim());
            }
        }
    }

    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }

    public String getField(String field){
        return this.fields.get(field);
    }

}
