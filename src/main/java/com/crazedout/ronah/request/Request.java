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

import com.crazedout.ronah.auth.User;
import com.crazedout.ronah.handler.MultipartPart;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Interface of a Request.
 * A Request object MUST ALWAYS be the first parameter in a Service method.
 * <code>
 *     public void getRest(<b>Request request</b>, @Param name, @Param value){}
 * </code>
 */
@SuppressWarnings("unused")
public interface Request {

    String getPath();
    Map<String,String> getHeaders();
    String getHeader(String key);
    Response getResponse();
    List<MultipartPart> getMultiParts();
    void setMultiParts(List<MultipartPart> multiParts);
    String getProtocol();
    String getMethod();
    String getQueryString();
    void setPostData(byte[] data);
    byte[] getPostData();
    String getParameter(String key, Charset charSet);
    String getParameter(String key);
    void setCharset(Charset charset);
    User getUser();
    }
