package com.sil.serverless.lambda.events;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;

/**
 * Wrapper on APIGatewayV2HTTPEvent. Used to extract common structure of events.
 *
 * @see APIGatewayV2HTTPEvent
 *
 * @author Albert Sikorski
 */
public final class ApiGatewayRequestEvent extends APIGatewayV2HTTPEvent implements HttpBasedEvent {

    public ApiGatewayRequestEvent(){
        super();
    }

    public ApiGatewayRequestEvent(APIGatewayV2HTTPEvent v2HTTPEvent){
        this(
                v2HTTPEvent.getVersion(),
                v2HTTPEvent.getRouteKey(),
                v2HTTPEvent.getRawPath(),
                v2HTTPEvent.getRawQueryString(),
                v2HTTPEvent.getCookies(),
                v2HTTPEvent.getHeaders(),
                v2HTTPEvent.getQueryStringParameters(),
                v2HTTPEvent.getPathParameters(),
                v2HTTPEvent.getStageVariables(),
                v2HTTPEvent.getBody(),
                v2HTTPEvent.getIsBase64Encoded(),
                v2HTTPEvent.getRequestContext()
        );
    }

    public ApiGatewayRequestEvent(String version, String routeKey, String rawPath, String rawQueryString, List<String> cookies, Map<String, String> headers, Map<String, String> queryStringParameters, Map<String, String> pathParameters, Map<String, String> stageVariables, String body, boolean isBase64Encoded, RequestContext requestContext) {
        super(version,routeKey,rawPath,rawQueryString,cookies,headers, queryStringParameters, pathParameters,stageVariables, body, isBase64Encoded,requestContext);
    }

    public static ApiGatewayRequestEvent from(APIGatewayV2HTTPEvent v2HTTPEvent){
        return new ApiGatewayRequestEvent(v2HTTPEvent);
    }

    @Override
    @JsonIgnore
    public String getHttpMethod() {
        if(getRequestContext() != null && getRequestContext().getHttp() != null){
            return getRequestContext().getHttp().getMethod();
        }
        return null;
    }

    @Override
    @JsonIgnore
    public String getPath() {
        return getRawPath();
    }
}
