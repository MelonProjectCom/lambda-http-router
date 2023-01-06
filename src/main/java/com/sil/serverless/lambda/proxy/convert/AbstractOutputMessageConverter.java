package com.sil.serverless.lambda.proxy.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Output message converter.
 * Based on method response type prepare response object.
 * Handle original response type or wrap method response object to body.
 *
 * @author Albert Sikorski
 *
 */
public abstract class AbstractOutputMessageConverter<T> implements OutputMessageConverter<T> {

    final static Logger logger = LoggerFactory.getLogger(AbstractOutputMessageConverter.class);

    private final ObjectMapper objectMapper;

    protected AbstractOutputMessageConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Based on event type prepare body of response.
     * If the response event type is the same as the final lambda response object, it simply returns the received object.
     * Otherwise, it translates the received object to body of final lambda response object.
     * In case of Void type. It returns only status code 202
     *
     * @param event Returned object from user method
     * @param type Type of object from user method
     * @return final lambda response object
     */
    public T output(Object event, Class<?> type) {
        int statusCode = 200;
        String body;

        if(isEventType(type)){
            return (T)event;
        }  else if(type.equals(String.class)){
            body = (String) event;
        }else if(type.equals(Void.class) || type.equals(Void.TYPE)){
            statusCode = 202;
            body = null;
        }else if (event == null) {
            body = null;
        }else {
            return convertToObject(event);
        }

        return withStatusCodeAndBody(statusCode, body);
    }

    abstract boolean isEventType(Class<?> parameterType);

    abstract T withStatusCodeAndBody(int statusCode, String body);


    private T convertToObject(Object event) {
        try {
            return withStatusCodeAndBody(200, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            logger.debug("Could not parse object to String... ", e);
            logger.error("Could not parse object to String...");
            return withStatusCodeAndBody(500, "Parsing error");
        }
    }
}
