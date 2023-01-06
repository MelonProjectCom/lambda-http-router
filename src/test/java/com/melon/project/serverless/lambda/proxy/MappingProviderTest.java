package com.melon.project.serverless.lambda.proxy;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.melon.project.serverless.lambda.bind.annotation.*;
import com.melon.project.serverless.lambda.proxy.convert.InputMessageConverter;
import com.melon.project.serverless.lambda.proxy.convert.OutputMessageConverter;
import com.melon.project.serverless.lambda.events.ApiGatewayRequestEvent;
import com.melon.project.serverless.lambda.testapp.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(classes = {
        TestApplication.class,
        MappingProviderTest.TestMethodLevelMapping.class,
        MappingProviderTest.TestClassLevelFunction.class
}, properties = "lambda.router.mode=API_GATEWAY")
public class MappingProviderTest {

    @LambdaController
    public static class TestMethodLevelMapping {

        @PathGetMapping("mapping/test/{id}")
        public String getMappingTest(){
            return "getMappingTest";
        }
        @PathPostMapping("mapping/test")
        public String postMappingTest(@Body String body){
            return body;
        }

        @PathPatchMapping("mapping/test/{id}")
        public String patchMappingTest(@Body String body){
            return body;
        }

        @PathPutMapping("mapping/test/{id}")
        public String putMappingTest(@Body String body){
            return body;
        }

        @PathDeleteMapping("mapping/test/{id}")
        public void deleteMappingTest(){

        }

        @PathGetMapping("mapping/function/test/{id}")
        public Function<ApiGatewayRequestEvent, APIGatewayV2HTTPResponse> getFunctionTest(){
            return event -> new APIGatewayV2HTTPResponse();
        }
    }

    @LambdaController
    @PathMapping( "mapping/function/class/test/{id}")
    public static class TestClassLevelFunction implements Function<ApiGatewayRequestEvent, APIGatewayV2HTTPResponse> {

        @Override
        public APIGatewayV2HTTPResponse apply(ApiGatewayRequestEvent apiGatewayRequestEvent) {
            return null;
        }
    }

    @Autowired
//    @MockBean
    ApplicationContext applicationContext;

    @Autowired
    InputMessageConverter<ApiGatewayRequestEvent> inputMessageConverter;
    @Autowired
    OutputMessageConverter<APIGatewayV2HTTPResponse> outputMessageConverter;


    @Autowired
    MappingProvider<ApiGatewayRequestEvent, APIGatewayV2HTTPResponse> mappingProvider;

    @Test
    void simpleDetectionTest() {
        Map<MethodPath, Function<ApiGatewayRequestEvent, APIGatewayV2HTTPResponse>> mappings = mappingProvider.mappings();

        assertFalse(mappings.isEmpty());
        assertEquals(7, mappings.size());
    }

}
