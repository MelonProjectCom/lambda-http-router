package com.sil.serverless.lambda.bind.annotation;

import com.sil.serverless.lambda.bind.RequestMethod;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;


/**
 * Used to specify mapping path for HTTP DELETE method
 *
 * @author Albert Sikorski
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@PathMapping(method = RequestMethod.DELETE)
@Documented
public @interface PathDeleteMapping {

    @AliasFor(annotation = PathMapping.class)
    String path() default "";

    @AliasFor(annotation = PathMapping.class)
    String value() default "";
}
