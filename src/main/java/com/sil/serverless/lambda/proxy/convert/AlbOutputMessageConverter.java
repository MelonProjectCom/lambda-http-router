package com.sil.serverless.lambda.proxy.convert;

import com.amazonaws.services.lambda.runtime.events.ApplicationLoadBalancerResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Output message converter for {@link ApplicationLoadBalancerResponseEvent} based events
 *
 * @author Albert Sikorski
 *
 */
public class AlbOutputMessageConverter extends AbstractOutputMessageConverter<ApplicationLoadBalancerResponseEvent> {

    public AlbOutputMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    boolean isEventType(Class<?> parameterType) {
        return parameterType.equals(ApplicationLoadBalancerResponseEvent.class);
    }

    @Override
    ApplicationLoadBalancerResponseEvent withStatusCodeAndBody(int statusCode, String body) {
        ApplicationLoadBalancerResponseEvent albResponse = new ApplicationLoadBalancerResponseEvent();
        albResponse.setBody(body);
        albResponse.setStatusCode(statusCode);
        return albResponse;
    }
}
