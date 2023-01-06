package com.melon.project.serverless.lambda.events;

import com.melon.project.serverless.lambda.filter.Filter;

import java.util.Map;

/**
 * Interface implemented by input events. Extract common fields useful in Filter.
 * It allows you to generalize input events and use common parts of those objects.
 *
 * @see Filter
 *
 * @author Albert Sikorski
 */
public sealed interface HttpBasedEvent permits AlbRequestEvent, ApiGatewayRequestEvent {

    String getHttpMethod();
    String getPath();

    void setBody(String body);
    String getBody();

    void setQueryStringParameters(Map<String, String> queryStringParameters);
    Map<String, String> getQueryStringParameters();

    void setHeaders(Map<String, String> headers);
    Map<String, String> getHeaders();

    void setIsBase64Encoded(boolean isBase64Encoded);
    boolean getIsBase64Encoded();
}
