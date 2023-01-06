package com.sil.serverless.lambda.bind.annotation;

import org.springframework.aot.hint.annotation.Reflective;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to parse path parameter by name to method parameter
 * Requires String, Integer or Double parameter
 * @author Albert Sikorski
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Reflective({LambdaControllerMappingReflectiveProcessor.class})
public @interface PathParameter {
    String name();
}
