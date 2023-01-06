package com.melon.project.serverless.lambda.exception;

import com.melon.project.serverless.lambda.filter.Filter;

/**
 * RouteValidationException can be thrown in Filter.
 * Code and message will be used in final event response
 *
 * @see Filter
 * @author Albert Sikorski
 */
public class RouteValidationException extends RuntimeException{

    public RouteValidationException(String message){
        super(message);
    }
}
