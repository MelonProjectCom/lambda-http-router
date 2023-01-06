package com.sil.serverless.lambda.exception;

/**
 * RouteValidationException can be thrown in Filter.
 * Code and message will be used in final event response
 *
 * @see com.sil.serverless.lambda.filter.Filter
 * @author Albert Sikorski
 */
public class RouteValidationException extends RuntimeException{

    public RouteValidationException(String message){
        super(message);
    }
}
