package com.crazedout.ronah.annotation;
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for HTTP request GET.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GET {
    /**
     * Http request endpoint e.e path.
     * @return String path
     */
    String path() default "/";

    /**
     * The Content-Type of this HTTP Response.
     * @return String content type
     */
    String response() default "application/json";

    /**
     * Content type that this method accepts.
     * @return String content type or * for wildcard.
     */
    String acceptContentType() default "*";

    /**
     * Ignore parent path if set by @PATH.
     * @return boolean true/false.
     */
    boolean ignoreParentPath() default false;

    /**
     * These methods REST will be protected by Basic Authentication.
     * @return boolean true/false.
     */
    boolean useBasicAuth() default false;

    /**
     * The Realm of a Basic Authentication.
     * @return String realm.
     */
    String basicAuthRealm() default "Ronah";
}
