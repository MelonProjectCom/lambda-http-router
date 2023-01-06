package com.sil.serverless.lambda.proxy.utils;

import com.sil.serverless.lambda.bind.RequestMethod;
import com.sil.serverless.lambda.bind.annotation.*;
import com.sil.serverless.lambda.proxy.MethodMapping;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.RepeatableContainers;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

/**
 * Annotation Utils class
 *
 * @author Albert Sikorski
 */
public class AnnotationUtils {
    public static Optional<MethodMapping> getMethodAndPath(Method method){
        Optional<PathMapping> pathMapping = MergedAnnotations.from(method, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.none())
                .get(PathMapping.class)
                .synthesize(MergedAnnotation::isPresent);

        if(pathMapping.isPresent()){
            RequestMethod requestMethod = pathMapping.get().method();
            String path = pathMapping.get().path();

            if(requestMethod != null && StringUtils.hasLength(path)){
                return Optional.of(new MethodMapping(method, requestMethod, path));
            }
        }
        return Optional.empty();
    }

    public static String getClassLevelPath(Class<?> clazz) {
        String path = "";
        if(clazz.isAnnotationPresent(PathMapping.class)){
            path = clazz.getAnnotation(PathMapping.class).path();
            if(!StringUtils.hasLength(path)){
                path = clazz.getAnnotation(PathMapping.class).value();
            }
        }
        return path;
    }

    public static boolean isBody(Parameter parameter) {
        return parameter.isAnnotationPresent(Body.class);
    }

    public static boolean isHeader(Parameter parameter) {
        return  parameter.isAnnotationPresent(Header.class);
    }

    public static boolean isHeaders(Parameter parameter) {
      return parameter.isAnnotationPresent(Headers.class);
    }

    public static boolean isPathParameter(Parameter parameter) {
        return parameter.isAnnotationPresent(PathParameter.class);
    }

    public static boolean isQueryParameter(Parameter parameter) {
        return parameter.isAnnotationPresent(QueryParameter.class);
    }

}
