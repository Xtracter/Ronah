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

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Interface of a Response.
 */
@SuppressWarnings("unused")
public interface Response {

    OutputStream getOutputStream();
    Response charset(Charset charset);
    Response ok(String data);
    Response contentType(String contentType);
    Response error();
    Response error(String message);
    Response forbidden();
    Response notFound();
    Response auth(String realm);
    void internalError(String message);
    void setContentType(String contentType);
    void setCharset(Charset charset);
    void setData(byte[] data);
    void send();
    void sendOptions();
    void addHeader(String key, String value);
    void applyCORSHeaders(Request request, String... allow);

}
