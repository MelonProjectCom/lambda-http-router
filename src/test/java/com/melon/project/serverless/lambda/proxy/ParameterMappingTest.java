package com.melon.project.serverless.lambda.proxy;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.melon.project.serverless.lambda.bind.annotation.*;
import com.melon.project.serverless.lambda.events.ApiGatewayRequestEvent;
import com.melon.project.serverless.lambda.testapp.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {
        TestApplication.class,
        ParameterMappingTest.TestMethodLevelMapping.class
}, properties = "lambda.router.mode=API_GATEWAY")
public class ParameterMappingTest {

    private static final String GET_INT_PATH_PARAM_PATH = "/mapping/test/pathParam/{id}";
    private static final String GET_QUERY_PARAM_PATH = "/mapping/test/query/";
    private static final String GET_HEADER_PARAM_PATH = "/mapping/test/header";
    private static final String GET_OPTIONAL_HEADER_PARAM_PATH = "/mapping/test/optional/header";
    private static final String GET_HEADERS_PARAM_PATH = "/mapping/test/headers";
    private static final String GET_WHOLE_REQUEST_PATH = "/mapping/test/whole";

    private static final String POST_WHOLE_REQUEST_PATH = "/mapping/test/whole";
    private static final String POST_STRING_BODY_PARAM_PATH = "/mapping/test/body/string";

    private static final String POST_OBJECT_BODY_PARAM_PATH = "/mapping/test/body/object";

    private static final String PUT_UNSUPPORTED_OBJECT_PARAM_PATH = "/mapping/test/unsupported/object";
    private static final String PUT_WHOLE_RESPONSE_OBJECT_PATH = "/mapping/test/whole/response";
    private static final String DELETE_VOID_RESPONSE_PATH = "/mapping/test/void/response";

    @LambdaController
    public static class TestMethodLevelMapping {

        public record TestObject(String name, int age){
        }

        @PathGetMapping(GET_INT_PATH_PARAM_PATH)
        public int getIntPathParameter(@PathParameter(name = "id") int id){
            return id;
        }

        @PathGetMapping(GET_QUERY_PARAM_PATH)
        public String getQueryParameter(@QueryParameter(name = "test") float queryParam,
                                        @QueryParameter(name = "test2") double queryParam2){
            return String.valueOf(queryParam + queryParam2);
        }

        @PathGetMapping(GET_HEADER_PARAM_PATH)
        public Boolean getHeaderParameter(@Header(name = "test") boolean testHeader){
            return testHeader;
        }

        @PathGetMapping(GET_OPTIONAL_HEADER_PARAM_PATH)
        public String getHeaderParameter(@Header(name = "test") Optional<String> testHeader){
            return testHeader.orElse("notFound");
        }

        @PathGetMapping(GET_HEADERS_PARAM_PATH)
        public Map<String, String> getHeadersParameter(@Headers Map<String, String> headers){
            return headers;
        }

        @PathGetMapping(GET_WHOLE_REQUEST_PATH)
        public ApiGatewayRequestEvent getObjectBodyParameter( ApiGatewayRequestEvent event){
            return event;
        }


        @PathPostMapping(POST_WHOLE_REQUEST_PATH)
        public APIGatewayV2HTTPEvent postObjectBodyParameter( APIGatewayV2HTTPEvent event){
            return event;
        }

        @PathPostMapping(POST_STRING_BODY_PARAM_PATH)
        public String postStringBodyParameter(@Body String body){
            return body;
        }

        @PathPostMapping(POST_OBJECT_BODY_PARAM_PATH)
        public TestObject postObjectBodyParameter(@Body TestObject body){
            return body;
        }

        @PathPutMapping(PUT_UNSUPPORTED_OBJECT_PARAM_PATH)
        public TestObject putUnsupportedObjectParameter(TestObject testObject){
            return testObject;
        }

        @PathPutMapping(PUT_WHOLE_RESPONSE_OBJECT_PATH)
        public APIGatewayV2HTTPResponse returnWholeLambdaResponseObject(@Body String body){
            APIGatewayV2HTTPResponse apiGatewayV2HTTPResponse = new APIGatewayV2HTTPResponse();
            apiGatewayV2HTTPResponse.setStatusCode(400);
            apiGatewayV2HTTPResponse.setBody(body);
            return apiGatewayV2HTTPResponse;
        }

        @PathDeleteMapping(DELETE_VOID_RESPONSE_PATH)
        public void voidResponse(@Body String body){
        }
    }

    @Autowired
    ApiGatewayMappingProxy apiGatewayMappingProxy;

    @Test
    void pathParamTest() {
        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("GET")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .withRawPath("/mapping/test/pathParam/99")
                .withQueryStringParameters(new HashMap<>())
                .build();

        APIGatewayV2HTTPResponse result = apiGatewayMappingProxy.proxy(ApiGatewayRequestEvent.from(event), null);

        assertEquals(200, result.getStatusCode());
        assertEquals("99", result.getBody());
    }

    @Test
    void headerParamTest() {
        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("GET")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .withRawPath(GET_HEADER_PARAM_PATH)
                .withQueryStringParameters(new HashMap<>())
                .withHeaders(Map.of("test", "true"))
                .build();

        APIGatewayV2HTTPResponse result = apiGatewayMappingProxy.proxy(ApiGatewayRequestEvent.from(event), null);

        assertEquals(200, result.getStatusCode());
        assertEquals("true", result.getBody());
    }

    @Test
    void queryParamTest() {
        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("GET")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .withRawPath(GET_QUERY_PARAM_PATH)
                .withQueryStringParameters(
                        Map.of(
                                "test", "4.5",
                                "test2", "5.50"
                        )
                )
                .build();

        APIGatewayV2HTTPResponse result = apiGatewayMappingProxy.proxy(ApiGatewayRequestEvent.from(event), null);

        assertEquals(200, result.getStatusCode());
        assertEquals("10.0", result.getBody());
    }

    @Test
    void optionalHeaderParamTest() {
        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("GET")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .withRawPath(GET_OPTIONAL_HEADER_PARAM_PATH)
                .withQueryStringParameters(new HashMap<>())
                .withHeaders(Map.of("test", "testValue"))
                .build();

        APIGatewayV2HTTPResponse result = apiGatewayMappingProxy.proxy(ApiGatewayRequestEvent.from(event), null);

        assertEquals(200, result.getStatusCode());
        assertEquals("testValue", result.getBody());
    }

    @Test
    void headersParamTest() {
        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("GET")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .withRawPath(GET_HEADERS_PARAM_PATH)
                .withQueryStringParameters(new HashMap<>())
                .withHeaders(Map.of("test", "testValue", "test2", "testValue2"))
                .build();

        APIGatewayV2HTTPResponse result = apiGatewayMappingProxy.proxy(ApiGatewayRequestEvent.from(event), null);

        assertEquals(200, result.getStatusCode());

        try{
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = mapper.readValue(result.getBody(), Map.class);

            assertEquals(2, map.size());
        }catch (IOException e){
            fail();
        }
    }

    @Test
    void stringBodyParamTest() {
        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("POST")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .withRawPath(POST_STRING_BODY_PARAM_PATH)
                .withQueryStringParameters(new HashMap<>())
                .withBody("testValue")
                .build();

        APIGatewayV2HTTPResponse result = apiGatewayMappingProxy.proxy(ApiGatewayRequestEvent.from(event), null);

        assertEquals(200, result.getStatusCode());
        assertEquals("testValue", result.getBody());
    }

    @Test
    void objectBodyParamTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        TestMethodLevelMapping.TestObject testObject = new TestMethodLevelMapping.TestObject("test", 18);
        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("POST")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .withRawPath(POST_STRING_BODY_PARAM_PATH)
                .withQueryStringParameters(new HashMap<>())
                .withBody(mapper.writeValueAsString(testObject))
                .build();


        APIGatewayV2HTTPResponse result = apiGatewayMappingProxy.proxy(ApiGatewayRequestEvent.from(event), null);

        assertEquals(200, result.getStatusCode());
        assertEquals(testObject, mapper.readValue(result.getBody(), TestMethodLevelMapping.TestObject.class));
    }

    @Test
    void wholeCustomEventTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("GET")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .withRawPath(GET_WHOLE_REQUEST_PATH)
                .withQueryStringParameters(new HashMap<>())
                .build();


        APIGatewayV2HTTPResponse result = apiGatewayMappingProxy.proxy(ApiGatewayRequestEvent.from(event), null);

        assertEquals(200, result.getStatusCode());
        assertEquals(event, mapper.readValue(result.getBody(), APIGatewayV2HTTPEvent.class));
    }


    @Test
    void wholeApiGatewayEventTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("POST")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .withRawPath(POST_WHOLE_REQUEST_PATH)
                .withBody("test")
                .withQueryStringParameters(new HashMap<>())
                .build();


        APIGatewayV2HTTPResponse result = apiGatewayMappingProxy.proxy(ApiGatewayRequestEvent.from(event), null);

        assertEquals(200, result.getStatusCode());
        assertEquals(event, mapper.readValue(result.getBody(), APIGatewayV2HTTPEvent.class));
    }

    @Test
    void unsupportedObjectTest() {
        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("PUT")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .withRawPath(PUT_UNSUPPORTED_OBJECT_PARAM_PATH)
                .withBody("test")
                .withQueryStringParameters(new HashMap<>())
                .build();


        APIGatewayV2HTTPResponse result = apiGatewayMappingProxy.proxy(ApiGatewayRequestEvent.from(event), null);

        assertEquals(200, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void wholeLambdaResponseObjectTest() {
        String body = "testBody";
        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("PUT")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .withRawPath(PUT_WHOLE_RESPONSE_OBJECT_PATH)
                .withBody(body)
                .withQueryStringParameters(new HashMap<>())
                .build();


        APIGatewayV2HTTPResponse result = apiGatewayMappingProxy.proxy(ApiGatewayRequestEvent.from(event), null);

        APIGatewayV2HTTPResponse apiGatewayV2HTTPResponse = new APIGatewayV2HTTPResponse();
        apiGatewayV2HTTPResponse.setStatusCode(400);
        apiGatewayV2HTTPResponse.setBody(body);

        assertEquals(400, result.getStatusCode());
        assertEquals(apiGatewayV2HTTPResponse, result);
    }

    @Test
    void voidResponseTest() {
        APIGatewayV2HTTPEvent.RequestContext.Http http = APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                .withMethod("DELETE")
                .build();
        APIGatewayV2HTTPEvent.RequestContext requestContext = APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(http)
                .build();
        APIGatewayV2HTTPEvent event = APIGatewayV2HTTPEvent.builder()
                .withRequestContext(requestContext)
                .withRawPath(DELETE_VOID_RESPONSE_PATH)
                .withQueryStringParameters(new HashMap<>())
                .build();


        APIGatewayV2HTTPResponse result = apiGatewayMappingProxy.proxy(ApiGatewayRequestEvent.from(event), null);

        assertEquals(202, result.getStatusCode());
        assertNull(result.getBody());
    }



}
