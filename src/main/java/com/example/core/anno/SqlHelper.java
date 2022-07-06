package com.example.core.anno;


import java.lang.annotation.*;

/**
 * @author chenwh3
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SqlHelper {

    String value() default "";

}
