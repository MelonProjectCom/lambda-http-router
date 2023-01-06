package com.melon.project.serverless.lambda.proxy.convert;

import java.util.Optional;

/**
 * Holds user mapping method parameters details
 *
 * @author Albert Sikorski
 *
 */
public record ParameterDetails(ParameterType parameterType, Class<?> type, Optional<String> value, boolean optional) {

    public ParameterDetails(ParameterType parameterType, Class<?> type){
        this(parameterType, type, Optional.empty(), false);
    }

    public ParameterDetails(ParameterType parameterType, Class<?> type, String value){
        this(parameterType, type, Optional.ofNullable(value), false);
    }
}
