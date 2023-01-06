package com.melon.project.serverless.lambda.entrypoint;

import com.amazonaws.services.lambda.runtime.events.ApplicationLoadBalancerRequestEvent;
import com.amazonaws.services.lambda.runtime.events.ApplicationLoadBalancerResponseEvent;
import com.melon.project.serverless.lambda.events.AlbRequestEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisabledInNativeImage
class AlbRequestHandlerFromInvokerTest {
    @Test
    void testMappingNotFound() {

        ApplicationLoadBalancerRequestEvent event = new ApplicationLoadBalancerRequestEvent();
        event.setHttpMethod("GET");
        event.setPath("/test");
        event.setQueryStringParameters(new HashMap<>());

        System.setProperty("MAIN_CLASS", "com.melon.project.serverless.lambda.testapp.TestApplication");
        System.setProperty("lambda.router.mode", "ALB");
        AlbRequestHandler albRequestHandler = new AlbRequestHandler();

        ApplicationLoadBalancerResponseEvent result = albRequestHandler.handleRequest(AlbRequestEvent.from(event), null);
        assertEquals("Not found", result.getBody());
    }
}
