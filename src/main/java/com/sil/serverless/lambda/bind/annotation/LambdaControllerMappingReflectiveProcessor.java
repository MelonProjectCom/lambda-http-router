package com.sil.serverless.lambda.bind.annotation;

import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.annotation.ReflectiveProcessor;
import org.springframework.core.MethodParameter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * ReflectiveProcessor implementation for LambdaController and specific annotated methods.
 * Register reflection hints for invoked annotated methods.
 *
 * @author Albert Sikorski
 */
class LambdaControllerMappingReflectiveProcessor implements ReflectiveProcessor {
    private final BindingReflectionHintsRegistrar bindingRegistrar = new BindingReflectionHintsRegistrar();

    @Override
    public void registerReflectionHints(ReflectionHints hints, AnnotatedElement element) {
        if (element instanceof Class<?> type) {
            this.registerTypeHints(hints, type);
        } else if (element instanceof Method method) {
            this.registerMethodHints(hints, method);
        }
    }

    protected void registerTypeHints(ReflectionHints hints, Class<?> type) {
        hints.registerType(type);
    }

    protected void registerMethodHints(ReflectionHints hints, Method method) {
        hints.registerMethod(method, ExecutableMode.INVOKE);
        Parameter[] parameters = method.getParameters();

        for (Parameter parameter : parameters) {
            this.registerParameterTypeHints(hints, MethodParameter.forParameter(parameter));
        }

        this.registerReturnTypeHints(hints, MethodParameter.forExecutable(method, -1));
    }

    protected void registerParameterTypeHints(ReflectionHints hints, MethodParameter methodParameter) {
        this.bindingRegistrar.registerReflectionHints(hints, methodParameter.getGenericParameterType());
    }

    protected void registerReturnTypeHints(ReflectionHints hints, MethodParameter returnTypeParameter) {
        this.bindingRegistrar.registerReflectionHints(hints, returnTypeParameter.getGenericParameterType());
    }
}
