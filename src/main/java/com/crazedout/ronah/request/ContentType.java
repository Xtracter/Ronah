package com.crazedout.ronah.request;
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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
public class ContentType {

    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public final static String APPLICATION_XML = "application/xml";
    public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String APPLICATION_JSON = "application/json";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_TEXT = "text/text";
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String IMAGE_JPEG = "image/jpeg";
    public static final String IMAGE_GIF = "image/gif";
    public static final String IMAGE_PNG = "image/png";
    protected String type;
    protected Charset charset = StandardCharsets.UTF_8;

    public ContentType(String type){
        this.type=type;
    }

    public ContentType(String type, Charset charset){
        this.type=type;
        this.charset=charset;
    }

    public void setType(String type){
        this.type = type;
    }

    public void setCharset(Charset charset){
        this.charset=charset;
    }

    public Charset getCharset(){
        return this.charset;
    }

}
