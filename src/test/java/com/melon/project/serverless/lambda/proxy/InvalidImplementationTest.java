package com.melon.project.serverless.lambda.proxy;

import com.melon.project.serverless.lambda.bind.annotation.LambdaController;
import com.melon.project.serverless.lambda.bind.annotation.PathGetMapping;
import com.melon.project.serverless.lambda.bind.annotation.QueryParameter;
import com.melon.project.serverless.lambda.exception.RoutePreparationException;
import com.melon.project.serverless.lambda.testapp.TestApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.boot.SpringApplication;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisabledInNativeImage
public class InvalidImplementationTest {

    @LambdaController
    public static class InvalidParameterTypeController {

        public record TestObject(String name, int age){
        }
        @PathGetMapping("/invalid/parameter")
        public String getInvalidParameterType(@QueryParameter(name = "test") TestObject queryParam){
            return queryParam.name;
        }

    }
    @LambdaController
    public static class DuplicatedRouteController {

        @PathGetMapping("/duplicated")
        public String first(){
            return "test";
        }

        @PathGetMapping("/duplicated")
        public String second(){
            return "test";
        }

    }

    @Test
    void invalidParameterTypeTest() {
        SpringApplication springApplication = new SpringApplication(TestApplication.class, InvalidImplementationTest.InvalidParameterTypeController.class);
        String[] properties = new String[] { "lambda.router.mode=API_GATEWAY"};
        assertThatThrownBy(() -> springApplication.run(properties)).hasRootCauseInstanceOf(RoutePreparationException.class);
    }

    @Test
    void duplicatedRouteTest() {
        SpringApplication springApplication = new SpringApplication(TestApplication.class, InvalidImplementationTest.DuplicatedRouteController.class);
        String[] properties = new String[] { "lambda.router.mode=API_GATEWAY"};

        springApplication.setMainApplicationClass(TestApplication.class);
        assertThatThrownBy(() -> springApplication.run(properties)).hasRootCauseInstanceOf(IllegalStateException.class)
                .rootCause()
                .hasMessageStartingWith("Duplicate key");
    }

}
