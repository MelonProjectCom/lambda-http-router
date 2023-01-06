package com.melon.project.serverless.lambda.proxy;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.melon.project.serverless.lambda.events.ApiGatewayRequestEvent;
import com.melon.project.serverless.lambda.filter.FilterChain;

/**
 * Handle events from AWS APIGateway.
 * Provides method to wrap status code and body to APIGateway response type.
 *
 * @author Albert Sikorski
 *
 */
public class ApiGatewayMappingProxy extends AbstractMappingProxy<ApiGatewayRequestEvent, APIGatewayV2HTTPResponse>{

    public ApiGatewayMappingProxy(MappingProvider<ApiGatewayRequestEvent, APIGatewayV2HTTPResponse> mappingProvider,
                                  MappingMatcher mappingMatcher,
                                  FilterChain filterChain) {
        super(mappingProvider.mappings(), mappingMatcher, filterChain);
    }

    @Override
    protected APIGatewayV2HTTPResponse withCodeAndBodyResponse(int statusCode, String body){
        return APIGatewayV2HTTPResponse.builder().withStatusCode(statusCode).withBody(body).build();
    }

}