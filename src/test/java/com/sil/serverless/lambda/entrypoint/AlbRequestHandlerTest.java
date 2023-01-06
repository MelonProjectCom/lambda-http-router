package com.sil.serverless.lambda.entrypoint;

import com.amazonaws.services.lambda.runtime.events.ApplicationLoadBalancerRequestEvent;
import com.amazonaws.services.lambda.runtime.events.ApplicationLoadBalancerResponseEvent;
import com.sil.serverless.lambda.bind.annotation.Body;
import com.sil.serverless.lambda.bind.annotation.LambdaController;
import com.sil.serverless.lambda.bind.annotation.PathPostMapping;
import com.sil.serverless.lambda.events.AlbRequestEvent;
import com.sil.serverless.lambda.testapp.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {TestApplication.class, AlbRequestHandlerTest.TestDummyController.class}, properties = "lambda.router.mode=ALB")
class AlbRequestHandlerTest {

    @LambdaController
    public static class TestDummyController {
        @PathPostMapping("dummy/test")
        public String dummy(@Body String body){
            return body;
        }
    }

    @Autowired
    AlbRequestHandler albRequestHandler;

    @Test
    void testEmptyHttpMethod() {
        ApplicationLoadBalancerRequestEvent event = new ApplicationLoadBalancerRequestEvent();

        ApplicationLoadBalancerResponseEvent response = albRequestHandler.handleRequest(AlbRequestEvent.from(event), null);
        assertEquals(400, response.getStatusCode());
        assertEquals("Invalid HTTP method", response.getBody());
    }

    @Test
    void testInvalidHttpMethod() {
        ApplicationLoadBalancerRequestEvent event = new ApplicationLoadBalancerRequestEvent();
        event.setHttpMethod("INVALID");

        ApplicationLoadBalancerResponseEvent response = albRequestHandler.handleRequest(AlbRequestEvent.from(event), null);
        assertEquals(400, response.getStatusCode());
        assertEquals("Invalid HTTP method", response.getBody());
    }

    @Test
    void testInvalidEmptyPath() {
        ApplicationLoadBalancerRequestEvent event = new ApplicationLoadBalancerRequestEvent();
        event.setHttpMethod("GET");

        ApplicationLoadBalancerResponseEvent response = albRequestHandler.handleRequest(AlbRequestEvent.from(event), null);
        assertEquals(400, response.getStatusCode());
        assertEquals("Invalid Path", response.getBody());

    }

    @Test
    void testInvalidPathValue() {
        ApplicationLoadBalancerRequestEvent event = new ApplicationLoadBalancerRequestEvent();
        event.setHttpMethod("GET");
        event.setPath("&dtest");

        ApplicationLoadBalancerResponseEvent response = albRequestHandler.handleRequest(AlbRequestEvent.from(event), null);
        assertEquals(400, response.getStatusCode());
        assertEquals("Invalid Path", response.getBody());

    }

    @Test
    void testMappingNotFound() {
        ApplicationLoadBalancerRequestEvent event = new ApplicationLoadBalancerRequestEvent();
        event.setHttpMethod("GET");
        event.setPath("/test");
        event.setQueryStringParameters(new HashMap<>());
        ApplicationLoadBalancerResponseEvent result = albRequestHandler.handleRequest(AlbRequestEvent.from(event), null);
        assertEquals("Not found", result.getBody());
    }

    @Test
    void testCorrectMapping() {
        ApplicationLoadBalancerRequestEvent event = new ApplicationLoadBalancerRequestEvent();
        event.setHttpMethod("POST");
        event.setPath("/dummy/test");
        event.setQueryStringParameters(new HashMap<>());
        event.setBody("Dummy handler");
        ApplicationLoadBalancerResponseEvent result = albRequestHandler.handleRequest(AlbRequestEvent.from(event), null);
        assertEquals("Dummy handler", result.getBody());
    }
}