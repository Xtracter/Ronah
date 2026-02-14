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

import com.crazedout.ronah.service.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipartPart {
    public Map<String, String> headers;
    public Map<String, String> fields;
    public byte[] body;

    /**
     * Creates a Multipart Part.
     * @param headers Map headers
     * @param body byte[] content.
     */
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

    /**
     * IS this a MultipartPart that is an upload File.
     * @return true/false.
     */
    public boolean isFile(){
        return this.headers.get("content-disposition").contains("; filename=\"");
    }

    /**
     * Gets the filename id this is an upload file
     * @return String filename.
     */
    public String getFileName(){
        String cd = this.headers.get("content-disposition");
        String[] tokens = cd.split(";");
        for(String t:tokens){
            if(t.trim().contains("filename=")){
                return t.trim().substring(10).replace("\"","");
            }
        }
        return null;
    }

    /**
     * Gets a headers value
     * @param key String key
     * @return String value
     */
    public String getHeader(String key) {
        return headers.get(key.toLowerCase());
    }

    public String getField(String field){
        return this.fields.get(field);
    }

    /**
     * Gets only the MutilpartPart's that is uploaded files.
     * @param request Request
     * @return List MultipartPart files uploads.
     */
    public static List<MultipartPart> getFileParts(Request request){
        return request.getMultiParts().stream().filter(MultipartPart::isFile).toList();
    }

}
