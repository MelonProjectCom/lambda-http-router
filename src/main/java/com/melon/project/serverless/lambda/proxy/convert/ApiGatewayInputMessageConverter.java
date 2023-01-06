package com.melon.project.serverless.lambda.proxy.convert;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.melon.project.serverless.lambda.events.ApiGatewayRequestEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Input message converter for {@link APIGatewayV2HTTPEvent} based events
 *
 * @author Albert Sikorski
 *
 */
public class ApiGatewayInputMessageConverter extends AbstractInputMessageConverter<ApiGatewayRequestEvent> {

    public ApiGatewayInputMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public boolean isEventType(Class<?> parameterType) {
        return parameterType.equals(APIGatewayV2HTTPEvent.class)
                || parameterType.equals(ApiGatewayRequestEvent.class);
    }

    @Override
    Map<String, String> getHeaders(ApiGatewayRequestEvent event) {
        if(event.getHeaders() == null){
            return new HashMap<>();
        }
        return event.getHeaders();
    }

    @Override
    String getPath(ApiGatewayRequestEvent event) {
        return event.getRawPath();
    }

    @Override
    String getQueryParam(ApiGatewayRequestEvent event, String name) {
        if(event.getQueryStringParameters() == null){
            return null;
        }
        return event.getQueryStringParameters().get(name);
    }

    @Override
    String getBody(ApiGatewayRequestEvent event) {
        return event.getBody();
    }
}
