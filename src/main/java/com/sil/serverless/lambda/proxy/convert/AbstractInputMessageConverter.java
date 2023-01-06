package com.sil.serverless.lambda.proxy.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sil.serverless.lambda.bind.annotation.Header;
import com.sil.serverless.lambda.bind.annotation.PathMapping;
import com.sil.serverless.lambda.bind.annotation.PathParameter;
import com.sil.serverless.lambda.bind.annotation.QueryParameter;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import static com.sil.serverless.lambda.proxy.convert.ParameterType.*;
import static com.sil.serverless.lambda.proxy.utils.AnnotationUtils.*;
import static com.sil.serverless.lambda.proxy.utils.ParameterValidationUtils.validateHeadersParameterType;
import static com.sil.serverless.lambda.proxy.utils.ParameterValidationUtils.validateParameterType;

/**
 * Input message converter. Design to translate input object parameters into method fields based on field
 * annotations.
 *
 * @author Albert Sikorski
 *
 */
public abstract class AbstractInputMessageConverter<T> implements InputMessageConverter<T>{
    final static Logger logger = LoggerFactory.getLogger(AbstractInputMessageConverter.class);

    private final ObjectMapper objectMapper;

    protected AbstractInputMessageConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Prepare method parameter details. Check if fields contains supported annotations.
     * Used in {@link #getInputVales(Object, List, String) getInputVales}
     * @param method Reflection details of mapped method.
     * @return Details about all fields from mapped method
     */
    public List<ParameterDetails> getInputParameters(Method method) {
        List<ParameterDetails> inputs = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            if(isEventType(parameter.getType())){
                inputs.add(new ParameterDetails(WHOLE_INPUT, parameter.getType()));
            }else{
                if(isBody(parameter)){
                    inputs.add(new ParameterDetails(BODY, parameter.getType()));
                } else if (isHeader(parameter)){
                    validateParameterType(method, parameter);
                    inputs.add(prepareParameterDetails(HEADER, parameter, parameter.getAnnotation(Header.class).name()));
                } else if ( isHeaders(parameter)){
                    validateHeadersParameterType(method, parameter);
                    inputs.add(prepareHeadersDetails(parameter));
                }else if (isPathParameter(parameter)){
                    validateParameterType(method, parameter);
                    inputs.add(prepareParameterDetails(PATH_PARAM, parameter, parameter.getAnnotation(PathParameter.class).name()));
                }else if (isQueryParameter(parameter)){
                    validateParameterType(method, parameter);
                    inputs.add(prepareParameterDetails(QUERY_PARAM, parameter, parameter.getAnnotation(QueryParameter.class).name()));
                }else {
                    inputs.add(new ParameterDetails(ParameterType.UNKNOWN, parameter.getType(),
                            Optional.empty(), parameter.getType().equals(Optional.class)));
                }
            }
        }
        return inputs;
    }

    /**
     * Converts input object into method fields based on parameter details
     * from {@link #getInputParameters(Method)} getInputParameters}
     *
     * @param event {@link com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent}
     *        or {@link com.amazonaws.services.lambda.runtime.events.ApplicationLoadBalancerRequestEvent}
     * @param inputs from {@link #getInputParameters(Method)} getInputParameters}
     * @param pathTemplate route value from {@link PathMapping}
     * @return method parameters values mapped from input event
     */
    public Object[] getInputVales(T event, List<ParameterDetails> inputs, String pathTemplate) {
        List<Object> inputValues = new ArrayList<>();
        inputs.forEach( input ->{
            switch (input.parameterType()){
                case PATH_PARAM -> inputValues.add(findPathParam(input, pathTemplate, event));
                case BODY -> inputValues.add(findBody(input, event));
                case QUERY_PARAM -> inputValues.add(findQueryParam(input, event));
                case HEADER -> inputValues.add(findHeader(input, event));
                case HEADERS -> inputValues.add(getHeaders(event));
                case WHOLE_INPUT -> inputValues.add(event);
                case UNKNOWN -> inputValues.add(unknownValue(input));
            }
        });
        return inputValues.toArray();
    }

    abstract boolean isEventType(Class<?> parameterType);

    abstract Map<String, String> getHeaders(T event);

    abstract String getPath(T event);

    abstract String getQueryParam(T event, String name);


    abstract String getBody(T event);

    private ParameterDetails prepareHeadersDetails(Parameter parameter) {
        var typeArguments = ((ParameterizedType)parameter.getParameterizedType()).getActualTypeArguments();
        if(typeArguments[0].equals(String.class) && typeArguments[1].equals(String.class)){
            return new ParameterDetails(ParameterType.HEADERS, parameter.getType());
        }else {
            return new ParameterDetails(ParameterType.UNKNOWN, parameter.getType());
        }
    }

    private ParameterDetails prepareParameterDetails(ParameterType parameterType, Parameter parameter, String value) {
        if(parameter.getType().equals(Optional.class)){
            var typeArguments = ((ParameterizedType)parameter.getParameterizedType()).getActualTypeArguments();
            if(typeArguments.length == 1 && typeArguments[0] instanceof Class<?> clazz){
                return new ParameterDetails(parameterType, clazz, Optional.ofNullable(value), true);
            }
            return new ParameterDetails(UNKNOWN, parameter.getType());
        }else {
            return new ParameterDetails(parameterType, parameter.getType(), value);
        }
    }
    private Object findHeader(ParameterDetails parameterDetails, T event) {
        String pathParamValue = parameterDetails.value()
                .map(value -> getHeaders(event).get(value))
                .orElse(null);
        return convertToType(pathParamValue, parameterDetails);

    }

    private Object findPathParam(ParameterDetails parameterDetails, String pathTemplate, T event) {
        String value = null;
        if(parameterDetails.value().isPresent()){
            List<String> templatePathSections = Arrays.asList(pathTemplate.split("/"));
            int matchingIndex = templatePathSections.indexOf(String.format("{%s}", parameterDetails.value().get()));
            if(matchingIndex >= 0){
                value = getPath(event).split("/")[matchingIndex];
            }
        }
        return convertToType(value, parameterDetails);
    }

    private Object findQueryParam(ParameterDetails parameterDetails, T event) {
        String queryParamValue = parameterDetails.value().map(value -> getQueryParam(event, value)).orElse(null);
        return convertToType(queryParamValue, parameterDetails);

    }

    private Object findBody(ParameterDetails parameterDetails, T event){
        Object body = null;
        if(parameterDetails.type().equals(String.class)){
            body = getBody(event);
        }else {
            try {
                body = objectMapper.readValue(getBody(event), parameterDetails.type());
            } catch (JsonProcessingException e) {
                logger.debug("Could not parse body to given object type...", e);
                logger.error("Could not parse body to given object type...");
            }
        }
        return body;
    }

    private Object convertToType(@Nullable String value, ParameterDetails parameterDetails) {
        Object finalValue = value;
        if(value != null){
            finalValue = toObject(parameterDetails.type(), value);
        }

        if(parameterDetails.optional()){
            return Optional.ofNullable(finalValue);
        }else {
            return finalValue;
        }
    }

    public static Object toObject( Class<?> clazz, String value ) {
        if( String.class == clazz ) return value;
        if( Object.class == clazz) return value;
        if( Boolean.class == clazz || Boolean.TYPE == clazz ) return Boolean.parseBoolean( value );
        if( Short.class == clazz || Short.TYPE == clazz ) return Short.parseShort( value );
        if( Integer.class == clazz || Integer.TYPE == clazz ) return Integer.parseInt( value );
        if( Long.class == clazz || Long.TYPE == clazz) return Long.parseLong( value );
        if( Float.class == clazz || Float.TYPE == clazz) return Float.parseFloat( value );
        if( Double.class == clazz || Double.TYPE == clazz) return Double.parseDouble( value );
        return null;
    }

    private Object unknownValue(ParameterDetails parameterDetails) {
        Class<?> type = parameterDetails.type();
        if(type.isPrimitive()){
            if( Boolean.TYPE == type) return false;
            if( Short.TYPE == type ) return (short) 0;
            if( Integer.TYPE == type ) return 0;
            if( Long.TYPE == type) return (long)0;
            if( Float.TYPE == type) return (float) 0;
            if( Double.TYPE == type) return (double) 0;
        }
        if(parameterDetails.optional()){
            return Optional.empty();
        }
        return null;
    }

}
