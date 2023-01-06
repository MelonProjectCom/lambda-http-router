package com.melon.project.serverless.lambda.proxy.utils;

import com.melon.project.serverless.lambda.exception.RoutePreparationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Optional;

/**
 * Method parameter validation utils
 *
 * @author Albert Sikorski
 */
public class ParameterValidationUtils {
    private final static Logger logger = LoggerFactory.getLogger(ParameterValidationUtils.class);

    public static void validateParameterType(Method method, Parameter parameter){
        boolean result = false;
        if(parameter.getType().equals(Optional.class)){
            var typeArguments = ((ParameterizedType)parameter.getParameterizedType()).getActualTypeArguments();
            if(typeArguments.length == 1 && typeArguments[0] instanceof Class<?> clazz){
                result = isBasicType(clazz);
            }
        }else {
            result = isBasicType(parameter.getType());
        }

        if(!result){
            throw new RoutePreparationException(
                    String.format("Unsupported parameter type in %s.%s parameter name: %s",
                            method.getDeclaringClass(), method.getName(), parameter.getName() )
            );
        }
    }

    public static void validateHeadersParameterType(Method method, Parameter parameter){
        boolean result = false;

        try{
            if(parameter.getType().equals(Map.class)
                    && parameter.getType().getTypeParameters().length == 2){
                var typeArguments = ((ParameterizedType)parameter.getParameterizedType()).getActualTypeArguments();
                result = typeArguments[0].equals(String.class) && typeArguments[1].equals(String.class);
            }
        }catch (ClassCastException e){
            logger.debug("Provided invalid Map parameter type");
        }


        if(!result){
            throw new RoutePreparationException(
                    String.format("Unsupported parameter type in %s.%s parameter name: %s",
                            method.getDeclaringClass(), method.getName(), parameter.getName() )
            );
        }
    }

    private static boolean isBasicType(Class<?> type){
        return type.equals(String.class) || type.equals(Object.class)
                || type.equals(Boolean.class) || type.equals(Boolean.TYPE)
                || type.equals(Short.class) || type.equals(Short.TYPE)
                || type.equals(Integer.class) || type.equals(Integer.TYPE)
                || type.equals(Long.class) || type.equals(Long.TYPE)
                || type.equals(Float.class) || type.equals(Float.TYPE)
                || type.equals(Double.class) || type.equals(Double.TYPE);
    }
}
