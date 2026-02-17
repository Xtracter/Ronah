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

/**
 * Class holding information about a Multipart/form-data content type.
 */
public class MultipartContentType extends ContentType{

    private final String boundary;

    /**
     * Creates a MultipartContentType
     * @param boundary String multipart boundary;
     */
    MultipartContentType(String boundary) {
        super(MULTIPART_FORM_DATA);
        this.boundary=boundary;
    }

    /**
     * Gets the original header format.
     * @return String header
     */
    public String getHeader(){
        return this.type + "; " + boundary;
    }

    /**
     * Gets the multipart boundary.
     * @return String boundary;
     */
    public String getBoundary(){
        return this.boundary;
    }

}
