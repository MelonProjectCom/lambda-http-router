package com.sil.serverless.lambda.proxy.convert;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sil.serverless.lambda.events.ApiGatewayRequestEvent;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiGatewayInputMessageConverterTest {

    ObjectMapper objectMapper = new ObjectMapper();
    @Test
    public void isEventTypeTest(){
        ApiGatewayInputMessageConverter converter = new ApiGatewayInputMessageConverter(objectMapper);

        assertTrue(converter.isEventType(APIGatewayV2HTTPEvent.class));
        assertTrue(converter.isEventType(ApiGatewayRequestEvent.class));
        assertFalse(converter.isEventType(Object.class));

    }

    @Test
    public void getHeadersTest(){
        ApiGatewayInputMessageConverter converter = new ApiGatewayInputMessageConverter(objectMapper);

        ApiGatewayRequestEvent validEvent = new ApiGatewayRequestEvent();
        validEvent.setHeaders(Map.of("test", "test"));

        assertEquals(1, converter.getHeaders(validEvent).size());

        validEvent.setHeaders(null);
        assertEquals(0, converter.getHeaders(validEvent).size());
    }

    @Test
    public void getPathTest(){
        ApiGatewayInputMessageConverter converter = new ApiGatewayInputMessageConverter(objectMapper);

        String path = "/test/path";
        ApiGatewayRequestEvent validEvent = new ApiGatewayRequestEvent();
        validEvent.setRawPath(path);

        assertEquals(path, converter.getPath(validEvent));
    }

    @Test
    public void getQueryParameterTest(){
        ApiGatewayInputMessageConverter converter = new ApiGatewayInputMessageConverter(objectMapper);
        ApiGatewayRequestEvent validEvent = new ApiGatewayRequestEvent();
        validEvent.setQueryStringParameters(Map.of("test", "test"));

        assertEquals("test", converter.getQueryParam(validEvent, "test"));
        assertNull(converter.getQueryParam(new ApiGatewayRequestEvent(), "test"));
    }

    @Test
    public void getBodyTest(){
        ApiGatewayInputMessageConverter converter = new ApiGatewayInputMessageConverter(objectMapper);
        ApiGatewayRequestEvent validEvent = new ApiGatewayRequestEvent();

        String body = "testBody";
        validEvent.setBody(body);

        assertEquals(body, converter.getBody(validEvent));
    }
}