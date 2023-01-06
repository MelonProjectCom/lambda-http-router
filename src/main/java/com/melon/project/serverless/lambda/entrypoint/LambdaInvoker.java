package com.melon.project.serverless.lambda.entrypoint;

import com.melon.project.serverless.lambda.utils.LambdaApplicationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

/**
 * Utility class to run Spring application context inside AWS lambda environment.
 * Used be supported Handlers.
 *
 * @see AlbRequestHandler
 * @see ApiGatewayRequestHandler
 *
 * @author Albert Sikorski
 */
public class LambdaInvoker {

    final static Logger logger = LoggerFactory.getLogger(LambdaInvoker.class);

    public static ApplicationContext getApplicationContext(){
        Class<?> startClass = LambdaApplicationUtils.getStartClass();
        String[] properties = new String[] { "--spring.main.web-application-type=none"};
        SpringApplication springApplication = new SpringApplication(startClass);
        logger.debug("Spring detect Main class:  {} Overriding", springApplication.getMainApplicationClass());
        springApplication.setMainApplicationClass(startClass);
        return springApplication.run(properties);
    }
}
