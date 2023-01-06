package com.sil.serverless.lambda.bind.annotation;

import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LambdaControllerMappingReflectiveProcessorTest {

    @LambdaController
    public static class TestControllerClass{

        @PathGetMapping("/test")
        public String testRoute(String parameter){ return "";}
    }
    @Test
    void shouldRegisterHints() throws NoSuchMethodException {
        RuntimeHints hints = new RuntimeHints();
//        ReflectionHints hints = new ReflectionHints();
        new LambdaControllerMappingReflectiveProcessor().registerReflectionHints(hints.reflection(), TestControllerClass.class);
        new LambdaControllerMappingReflectiveProcessor().registerReflectionHints(hints.reflection(), TestControllerClass.class.getMethod("testRoute", String.class));

        assertTrue(RuntimeHintsPredicates.reflection().onType(TestControllerClass.class).test(hints));

        assertTrue(RuntimeHintsPredicates.reflection().onMethod(TestControllerClass.class.getMethod("testRoute", String.class)).test(hints));
    }
}