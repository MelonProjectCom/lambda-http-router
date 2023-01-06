package com.melon.project.serverless.lambda.filter.validation;

import com.melon.project.serverless.lambda.filter.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class - loads default filters into application context
 *
 * @author Albert Sikorski
 */
@Configuration
public class ValidationFilterConfiguration {
    @Bean
    public Filter httpMethodValidationFilter(){
        return new HttpMethodValidationFilter();
    }
    @Bean
    public Filter pathValidationFilter(){
        return new PathValidationFilter();
    }
    @Bean
    public Filter eventStructureValidation(){
        return new EventStructureValidationFilter();
    }
}
