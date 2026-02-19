package com.crazedout.ronah.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OPTIONS {

    String path() default "";

    /**
     * Ignore parent path if set by @PATH.
     * @return boolean true/false.
     */
    boolean ignoreParentPath() default false;

}
