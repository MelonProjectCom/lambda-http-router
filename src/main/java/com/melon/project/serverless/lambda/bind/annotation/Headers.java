package com.melon.project.serverless.lambda.bind.annotation;

import org.springframework.aot.hint.annotation.Reflective;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to parse event headers to method parameter as key/value Map
 *
 * @author Albert Sikorski
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Reflective({LambdaControllerMappingReflectiveProcessor.class})
public @interface Headers {
}
