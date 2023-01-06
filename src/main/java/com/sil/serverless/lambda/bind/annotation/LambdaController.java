package com.sil.serverless.lambda.bind.annotation;

import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;

import java.lang.annotation.*;


/**
 * Used to mark class as Lambda controller and register bean as potential route
 * @author Albert Sikorski
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Controller
@Reflective({LambdaControllerMappingReflectiveProcessor.class})
public @interface LambdaController {

    @AliasFor(
            annotation = Controller.class
    )
    String value() default "";
}
