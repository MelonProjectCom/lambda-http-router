package com.sil.serverless.lambda.events;


import com.amazonaws.services.lambda.runtime.events.ApplicationLoadBalancerRequestEvent;

/**
 * Wrapper on ApplicationLoadBalancerRequestEvent. Used to extract common structure of events.
 *
 * @see ApplicationLoadBalancerRequestEvent
 *
 * @author Albert Sikorski
 */
public final class AlbRequestEvent extends ApplicationLoadBalancerRequestEvent implements HttpBasedEvent {

    public AlbRequestEvent(){
        super();
    }

    public AlbRequestEvent(ApplicationLoadBalancerRequestEvent albEvent){
        super();
        setRequestContext(albEvent.getRequestContext());
        setHttpMethod(albEvent.getHttpMethod());
        setPath(albEvent.getPath());
        setQueryStringParameters(albEvent.getQueryStringParameters());
        setHeaders(albEvent.getHeaders());
        setMultiValueHeaders(albEvent.getMultiValueHeaders());
        setBody(albEvent.getBody());
        setIsBase64Encoded(albEvent.getIsBase64Encoded());
    }
    public static AlbRequestEvent from(ApplicationLoadBalancerRequestEvent albEvent){
        return new AlbRequestEvent(albEvent);
    }
}
