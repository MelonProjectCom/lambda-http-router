package com.sil.serverless.lambda.entrypoint;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sil.serverless.lambda.bind.annotation.Body;
import com.sil.serverless.lambda.bind.annotation.LambdaController;
import com.sil.serverless.lambda.bind.annotation.PathGetMapping;
import com.sil.serverless.lambda.bind.annotation.PathPostMapping;
import com.sil.serverless.lambda.events.ApiGatewayRequestEvent;
import com.sil.serverless.lambda.testapp.DummyLambdaContext;
import com.sil.serverless.lambda.testapp.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {TestApplication.class, ApiGatewayRequestHandlerTest.TestDummyController.class}, properties = "lambda.router.mode=API_GATEWAY")
class ApiGatewayRequestHandlerTest {
    @LambdaController
    public static class TestDummyController {

        @Autowired
        private Context lambdaContext;

        @PathPostMapping("dummy/test")
        public String dummy(@Body String body){
            return body;
        }

        @PathGetMapping("context/test")
        public String contextTest(){
            return lambdaContext.getAwsRequestId();
        }

        @PathGetMapping("/dummy/exception/test")
        public String unhandledException(){
            throw new RuntimeException("Unhandled exception....");
        }

    }

    @Autowired
    ApiGatewayRequestHandler apiGatewayRequestHandler;

    @Test
    void testEmptyHttpMethod() {
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder().build();
        APIGatewayV2HTTPResponse response = apiGatewayRequestHandler.handleRequest(ApiGatewayRequestEvent.from(event), null);
        assertEquals(400, response.getStatusCode());
        assertEquals("Invalid HTTP method", response.getBody());

    }

    @Test
    void testInvalidHttpMethod() {


        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("INVALID")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .build();
        APIGatewayV2HTTPResponse response = apiGatewayRequestHandler.handleRequest(ApiGatewayRequestEvent.from(event), null);
        assertEquals(400, response.getStatusCode());
        assertEquals("Invalid HTTP method", response.getBody());
    }

    @Test
    void testInvalidEmptyPath() {
        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("GET")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .build();

        APIGatewayV2HTTPResponse response = apiGatewayRequestHandler.handleRequest(ApiGatewayRequestEvent.from(event), null);
        assertEquals(400, response.getStatusCode());
        assertEquals("Invalid Path", response.getBody());
    }

    @Test
    void testInvalidPathValue() {
        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("GET")
                .withPath("&dtest")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .build();

        APIGatewayV2HTTPResponse response = apiGatewayRequestHandler.handleRequest(ApiGatewayRequestEvent.from(event), null);
        assertEquals(400, response.getStatusCode());
        assertEquals("Invalid Path", response.getBody());

    }

    @Test
    void testInvalidRequestStructureGetValue() {
        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("GET")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .withRawPath("/test")
                .withBody("Body shouldn't be passed into GET method")
                .build();

        APIGatewayV2HTTPResponse response = apiGatewayRequestHandler.handleRequest(ApiGatewayRequestEvent.from(event), null);
        assertEquals(400, response.getStatusCode());
        assertEquals("Invalid Event - body not allowed for method: GET", response.getBody());

    }

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

        APIGatewayV2HTTPResponse result = apiGatewayRequestHandler.handleRequest(ApiGatewayRequestEvent.from(event), null);
        assertEquals("Not found", result.getBody());
    }

    @Test
    void testCorrectMapping() {
        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("POST")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .withRawPath("/dummy/test")
                .withQueryStringParameters(new HashMap<>())
                .withBody("Dummy handler")
                .build();

        APIGatewayV2HTTPResponse result = apiGatewayRequestHandler.handleRequest(ApiGatewayRequestEvent.from(event), null);
        assertEquals("Dummy handler", result.getBody());
    }

    @Test
    void unhandledExceptionTest() {
        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("GET")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .withRawPath("/dummy/exception/test")
                .build();

        APIGatewayV2HTTPResponse result = apiGatewayRequestHandler.handleRequest(ApiGatewayRequestEvent.from(event), null);
        assertEquals(500, result.getStatusCode());
        assertEquals("Internal server error", result.getBody());
    }

    @Test
    void contextTest() {
        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("GET")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .withRawPath("/context/test")
                .build();

        String testRequestID = "testAwsRequestId";
        Context testLambdaContext = new DummyLambdaContext(testRequestID);

        APIGatewayV2HTTPResponse result = apiGatewayRequestHandler.handleRequest(ApiGatewayRequestEvent.from(event), testLambdaContext);

        assertEquals(testRequestID, result.getBody());
    }
}
