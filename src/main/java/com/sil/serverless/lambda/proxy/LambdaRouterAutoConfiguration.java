package com.sil.serverless.lambda.proxy;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.amazonaws.services.lambda.runtime.events.ApplicationLoadBalancerResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sil.serverless.lambda.entrypoint.AlbRequestHandler;
import com.sil.serverless.lambda.entrypoint.ApiGatewayRequestHandler;
import com.sil.serverless.lambda.events.AlbRequestEvent;
import com.sil.serverless.lambda.events.ApiGatewayRequestEvent;
import com.sil.serverless.lambda.filter.Filter;
import com.sil.serverless.lambda.filter.FilterChain;
import com.sil.serverless.lambda.filter.FilterChainImpl;
import com.sil.serverless.lambda.filter.validation.ValidationFilterConfiguration;
import com.sil.serverless.lambda.proxy.context.StaticLambdaContext;
import com.sil.serverless.lambda.proxy.convert.AlbInputMessageConverter;
import com.sil.serverless.lambda.proxy.convert.AlbOutputMessageConverter;
import com.sil.serverless.lambda.proxy.convert.ApiGatewayInputMessageConverter;
import com.sil.serverless.lambda.proxy.convert.ApiGatewayOutputMessageConverter;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

import java.util.List;

/**
 * Configuration class - provides required beans based on router mode (APIGateway or ALB)
 *
 * @author Albert Sikorski
 *
 */
@AutoConfiguration
@Import(ValidationFilterConfiguration.class)
public class LambdaRouterAutoConfiguration {

    private final static Logger logger = LoggerFactory.getLogger(LambdaRouterAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    @Bean
    public FilterChain filterChain(List<Filter> filterList){
        return new FilterChainImpl(filterList);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Context getLambdaContext(){
        return StaticLambdaContext.getContext();
    }


    @ConditionalOnProperty(value = "lambda.router.mode", havingValue = "API_GATEWAY", matchIfMissing = true)
    @Configuration
    public static class ApiGatewayConfiguration{


        @PostConstruct
        public void init(){
            logger.info("Lambda router - API_GATEWAY mode enabled");
        }

        @Bean
        public ApiGatewayInputMessageConverter inputMessageConverter(ObjectMapper objectMapper){
            return new ApiGatewayInputMessageConverter(objectMapper);
        }

        @Bean
        public ApiGatewayOutputMessageConverter outputMessageConverter(ObjectMapper objectMapper){
            return new ApiGatewayOutputMessageConverter(objectMapper);
        }

        @Bean
        public MappingProvider<ApiGatewayRequestEvent, APIGatewayV2HTTPResponse> mappingProvider(ApplicationContext applicationContext,
                                                                                                ApiGatewayInputMessageConverter inputMessageConverter,
                                                                                                ApiGatewayOutputMessageConverter outputMessageConverter){
            return new MappingProvider<>(ApiGatewayRequestEvent.class,
                    APIGatewayV2HTTPResponse.class,
                    applicationContext,
                    inputMessageConverter,
                    outputMessageConverter);
        }

        @Bean
        public ApiGatewayMappingProxy mappingProxy(MappingProvider<ApiGatewayRequestEvent, APIGatewayV2HTTPResponse> mappingProvider,
                                                   MappingMatcher mappingMatcher,
                                                   FilterChain filterChain){
            return new ApiGatewayMappingProxy(mappingProvider, mappingMatcher, filterChain);
        }

        @Bean
        public ApiGatewayRequestHandler lambdaApiGatewayHandler(ApiGatewayMappingProxy apiGatewayMappingProxy){
            return new ApiGatewayRequestHandler(apiGatewayMappingProxy);
        }

        @Bean
        public MappingMatcher mappingMatcher(MappingProvider<ApiGatewayRequestEvent, APIGatewayV2HTTPResponse> mappingProvider){
            return new MappingMatcher(mappingProvider.mappings().keySet());
        }
    }

    @ConditionalOnProperty(value = "lambda.router.mode", havingValue = "ALB")
    @Configuration
    public static class AlbConfiguration{

        @PostConstruct
        public void init(){
            logger.info("Lambda router - ALB mode enabled");
        }

        @Bean
        public AlbInputMessageConverter inputMessageConverter(ObjectMapper objectMapper){
            return new AlbInputMessageConverter(objectMapper);
        }

        @Bean
        public AlbOutputMessageConverter outputMessageConverter(ObjectMapper objectMapper){
            return new AlbOutputMessageConverter(objectMapper);
        }

        @Bean
        public MappingProvider<AlbRequestEvent, ApplicationLoadBalancerResponseEvent> mappingProvider(ApplicationContext applicationContext,
                                                                                                                          AlbInputMessageConverter inputMessageConverter,
                                                                                                                          AlbOutputMessageConverter outputMessageConverter){
            return new MappingProvider<>(AlbRequestEvent.class, ApplicationLoadBalancerResponseEvent.class, applicationContext, inputMessageConverter, outputMessageConverter);
        }

        @Bean
        public AlbMappingProxy mappingProxy(MappingProvider<AlbRequestEvent, ApplicationLoadBalancerResponseEvent> mappingProvider,
                                            MappingMatcher mappingMatcher,
                                            FilterChain filterChain){
            return new AlbMappingProxy(mappingProvider, mappingMatcher, filterChain);
        }

        @Bean
        public AlbRequestHandler lambdaAlbHandler(AlbMappingProxy mappingProxy){
            return new AlbRequestHandler(mappingProxy);
        }

        @Bean
        public MappingMatcher mappingMatcher(MappingProvider<AlbRequestEvent, ApplicationLoadBalancerResponseEvent> mappingProvider){
            return new MappingMatcher(mappingProvider.mappings().keySet());
        }
    }
}
