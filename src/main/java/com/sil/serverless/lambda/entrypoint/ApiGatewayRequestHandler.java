package com.sil.serverless.lambda.entrypoint;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.sil.serverless.lambda.events.ApiGatewayRequestEvent;
import com.sil.serverless.lambda.proxy.ApiGatewayMappingProxy;

/**
 * Dedicated handler for requests from API Gateway.
 * Default constructor starts Spring boot context and load required beans from it.
 *
 * @see com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
 * @see LambdaInvoker
 *
 * @author Albert Sikorski
 */
public class ApiGatewayRequestHandler implements RequestHandler<ApiGatewayRequestEvent, APIGatewayV2HTTPResponse> {

    private final ApiGatewayMappingProxy apiGatewayMappingProxy;

    public ApiGatewayRequestHandler(){
        apiGatewayMappingProxy = LambdaInvoker.getApplicationContext().getBean(ApiGatewayMappingProxy.class);
    }
    public ApiGatewayRequestHandler(ApiGatewayMappingProxy apiGatewayMappingProxy) {
        this.apiGatewayMappingProxy = apiGatewayMappingProxy;
    }

    /**
     * Handles a Lambda Function request from API Gateway
     * @param apiGatewayV2HTTPEvent The Lambda Function input
     * @param context The Lambda execution environment context object.
     * @return API Gateway response valid format object
     */
    @Override
    public APIGatewayV2HTTPResponse handleRequest(ApiGatewayRequestEvent apiGatewayV2HTTPEvent, Context context) {
        return apiGatewayMappingProxy.proxy(apiGatewayV2HTTPEvent, context);
    }
}
