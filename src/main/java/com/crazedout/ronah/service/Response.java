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

import java.io.OutputStream;


/**
 * Interface of a Response.
 */
@SuppressWarnings("unused")
public interface Response {

    OutputStream getOutputStream();
    Response ok(String data);
    Response ok(String contentType, String data);
    Response error();
    Response forbidden();
    Response notFound();
    void internalError(String message);
    void setContentType(String contentType);
    void setData(String data);
    void send();
    void addHeader(String key, String value);

}
