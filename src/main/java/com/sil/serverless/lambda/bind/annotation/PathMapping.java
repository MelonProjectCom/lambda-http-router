package com.sil.serverless.lambda.bind.annotation;

import com.sil.serverless.lambda.bind.RequestMethod;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Used to specify route path and HTTP method
 * When used on class path param is used as prefix for method level mappings
 *
 * @author Albert Sikorski
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Reflective({LambdaControllerMappingReflectiveProcessor.class})
public @interface PathMapping {

    RequestMethod method() default RequestMethod.GET;

    @AliasFor("path")
    String value() default "";

    @AliasFor("value")
    String path() default "";
}
