package com.melon.project.serverless.lambda.entrypoint;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ApplicationLoadBalancerResponseEvent;
import com.melon.project.serverless.lambda.events.AlbRequestEvent;
import com.melon.project.serverless.lambda.proxy.AlbMappingProxy;

/**
 * Dedicated handler for requests from Application Load Balancer.
 * Default constructor starts Spring boot context and load required beans from it.
 *
 * @see com.amazonaws.services.lambda.runtime.events.ApplicationLoadBalancerRequestEvent
 * @see LambdaInvoker
 *
 * @author Albert Sikorski
 */
public class AlbRequestHandler implements RequestHandler<AlbRequestEvent, ApplicationLoadBalancerResponseEvent> {

    private final AlbMappingProxy mappingProxy;

    public AlbRequestHandler(){
        mappingProxy = LambdaInvoker.getApplicationContext().getBean(AlbMappingProxy.class);
    }
    public AlbRequestHandler(AlbMappingProxy mappingProxy) {
        this.mappingProxy = mappingProxy;
    }


    /**
     * Handles a Lambda Function request from Application Load Balancer
     * @param applicationLoadBalancerRequestEvent The Lambda Function input
     * @param context The Lambda execution environment context object.
     * @return ALB response valid format object
     */
    @Override
    public ApplicationLoadBalancerResponseEvent handleRequest(AlbRequestEvent applicationLoadBalancerRequestEvent, Context context) {
        return mappingProxy.proxy(applicationLoadBalancerRequestEvent, context);
    }
}
