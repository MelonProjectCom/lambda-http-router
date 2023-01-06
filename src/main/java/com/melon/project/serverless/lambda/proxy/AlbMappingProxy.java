package com.melon.project.serverless.lambda.proxy;

import com.amazonaws.services.lambda.runtime.events.ApplicationLoadBalancerResponseEvent;
import com.melon.project.serverless.lambda.events.AlbRequestEvent;
import com.melon.project.serverless.lambda.filter.FilterChain;

/**
 * Handle events from AWS ApplicationLoadBalancer.
 * Provides method to wrap status code and body to ALB response type.
 *
 * @author Albert Sikorski
 *
 */
public class AlbMappingProxy extends AbstractMappingProxy<AlbRequestEvent, ApplicationLoadBalancerResponseEvent>{

    public AlbMappingProxy(MappingProvider<AlbRequestEvent, ApplicationLoadBalancerResponseEvent> mappingProvider,
                           MappingMatcher mappingMatcher,
                           FilterChain filterChain) {
        super(mappingProvider.mappings(), mappingMatcher, filterChain);
    }

    @Override
    protected  ApplicationLoadBalancerResponseEvent withCodeAndBodyResponse(int statusCode, String body) {
        ApplicationLoadBalancerResponseEvent albResponse = new ApplicationLoadBalancerResponseEvent();
        albResponse.setStatusCode(statusCode);
        albResponse.setBody(body);
        return albResponse;
    }
}