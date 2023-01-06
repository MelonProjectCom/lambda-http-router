package com.sil.serverless.lambda.proxy.convert;

import com.amazonaws.services.lambda.runtime.events.ApplicationLoadBalancerRequestEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sil.serverless.lambda.events.AlbRequestEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Input message converter for {@link ApplicationLoadBalancerRequestEvent} based events
 *
 * @author Albert Sikorski
 *
 */
public class AlbInputMessageConverter extends AbstractInputMessageConverter<AlbRequestEvent> {

    public AlbInputMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public boolean isEventType(Class<?> parameterType) {
        return parameterType.equals(ApplicationLoadBalancerRequestEvent.class)
                || parameterType.equals(AlbRequestEvent.class);
    }

    @Override
    Map<String, String> getHeaders(AlbRequestEvent event) {
        if(event.getHeaders() == null){
            return new HashMap<>();
        }
        return event.getHeaders();
    }

    @Override
    String getPath(AlbRequestEvent event) {
        return event.getPath();
    }

    @Override
    String getQueryParam(AlbRequestEvent event, String name) {
        if(event.getQueryStringParameters() == null){
            return null;
        }
        return event.getQueryStringParameters().get(name);
    }

    @Override
    String getBody(AlbRequestEvent event) {
        return event.getBody();
    }
}