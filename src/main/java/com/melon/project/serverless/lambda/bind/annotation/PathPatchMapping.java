package com.melon.project.serverless.lambda.bind.annotation;

import com.melon.project.serverless.lambda.bind.RequestMethod;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Used to specify mapping path for HTTP PATCH method
 *
 * @author Albert Sikorski
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@PathMapping(method = RequestMethod.PATCH)
@Documented
public @interface PathPatchMapping {

    @AliasFor(annotation = PathMapping.class)
    String path() default "";

    @AliasFor(annotation = PathMapping.class)
    String value() default "";
}
