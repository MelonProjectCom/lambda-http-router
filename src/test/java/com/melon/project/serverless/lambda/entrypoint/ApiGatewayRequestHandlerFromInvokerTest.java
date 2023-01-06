package com.melon.project.serverless.lambda.entrypoint;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.melon.project.serverless.lambda.events.ApiGatewayRequestEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisabledInNativeImage
class ApiGatewayRequestHandlerFromInvokerTest {

    @Test
    void testMappingNotFound() {

        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("GET")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .withRawPath("/test")
                .withQueryStringParameters(new HashMap<>())
                .build();

        System.setProperty("MAIN_CLASS", "com.melon.project.serverless.lambda.testapp.TestApplication");
        System.setProperty("lambda.router.mode", "API_GATEWAY");
        ApiGatewayRequestHandler apiGatewayRequestHandler = new ApiGatewayRequestHandler();
        APIGatewayV2HTTPResponse result = apiGatewayRequestHandler.handleRequest(ApiGatewayRequestEvent.from(event), null);
        assertEquals("Not found", result.getBody());

    }
}
