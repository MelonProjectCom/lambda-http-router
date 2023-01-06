package com.melon.project.serverless.lambda.proxy;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.melon.project.serverless.lambda.exception.RouteAccessException;
import com.melon.project.serverless.lambda.exception.RouteReflectionException;
import com.melon.project.serverless.lambda.events.ApiGatewayRequestEvent;
import com.melon.project.serverless.lambda.filter.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@DisabledInNativeImage
class ApiGatewayMappingProxyTest {

    MappingProvider<ApiGatewayRequestEvent, APIGatewayV2HTTPResponse> mappingProvider = Mockito.mock(MappingProvider.class);
    MappingMatcher mappingMatcher = Mockito.mock(MappingMatcher.class);
    FilterChain filterChain = Mockito.mock(FilterChain.class);

    @Test
    public void routeAccessExceptionTest(){
        Mockito.doThrow(new RouteAccessException(RouteAccessException.ResponseCode.UNAUTHORIZED, "unauthorized"))
                .when(filterChain).doFilter(any(), any());

        ApiGatewayMappingProxy apiGatewayMappingProxy = new ApiGatewayMappingProxy(mappingProvider, mappingMatcher, filterChain);
        APIGatewayV2HTTPResponse response = apiGatewayMappingProxy.proxy(null, null);

        assertEquals(401, response.getStatusCode());
        assertEquals("unauthorized", response.getBody());
    }

    @Test
    public void routeReflectionExceptionTest(){
        Mockito.doThrow(new RouteReflectionException("Couldn't reflect method...", new IllegalAccessException()))
                .when(filterChain).doFilter(any(), any());

        ApiGatewayMappingProxy apiGatewayMappingProxy = new ApiGatewayMappingProxy(mappingProvider, mappingMatcher, filterChain);
        APIGatewayV2HTTPResponse response = apiGatewayMappingProxy.proxy(null, null);

        assertEquals(500, response.getStatusCode());
        assertEquals("Internal server error", response.getBody());
    }

}