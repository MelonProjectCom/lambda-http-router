package com.melon.project.serverless.lambda.proxy.convert;

import com.amazonaws.services.lambda.runtime.events.ApplicationLoadBalancerRequestEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.melon.project.serverless.lambda.events.AlbRequestEvent;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AlbInputMessageConverterTest {

    ObjectMapper objectMapper = new ObjectMapper();
    @Test
    public void isEventTypeTest(){
        AlbInputMessageConverter converter = new AlbInputMessageConverter(objectMapper);

        assertTrue(converter.isEventType(ApplicationLoadBalancerRequestEvent.class));
        assertTrue(converter.isEventType(AlbRequestEvent.class));
        assertFalse(converter.isEventType(Object.class));

    }

    @Test
    public void getHeadersTest(){
        AlbInputMessageConverter converter = new AlbInputMessageConverter(objectMapper);

        AlbRequestEvent validEvent = new AlbRequestEvent();
        validEvent.setHeaders(Map.of("test", "test"));

        assertEquals(1, converter.getHeaders(validEvent).size());

        validEvent.setHeaders(null);
        assertEquals(0, converter.getHeaders(validEvent).size());
    }

    @Test
    public void getPathTest(){
        AlbInputMessageConverter converter = new AlbInputMessageConverter(objectMapper);

        String path = "/test/path";
        AlbRequestEvent validEvent = new AlbRequestEvent();
        validEvent.setPath(path);

        assertEquals(path, converter.getPath(validEvent));
    }

    @Test
    public void getQueryParameterTest(){
        AlbInputMessageConverter converter = new AlbInputMessageConverter(objectMapper);
        AlbRequestEvent validEvent = new AlbRequestEvent();
        validEvent.setQueryStringParameters(Map.of("test", "test"));

        assertEquals("test", converter.getQueryParam(validEvent, "test"));
        assertNull(converter.getQueryParam(new AlbRequestEvent(), "test"));
    }

    @Test
    public void getBodyTest(){
        AlbInputMessageConverter converter = new AlbInputMessageConverter(objectMapper);
        AlbRequestEvent validEvent = new AlbRequestEvent();

        String body = "testBody";
        validEvent.setBody(body);

        assertEquals(body, converter.getBody(validEvent));
    }
}