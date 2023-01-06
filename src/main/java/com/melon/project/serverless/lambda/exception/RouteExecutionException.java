package com.melon.project.serverless.lambda.exception;

/**
 * RouteExecutionException can be thrown when route method can't be accessed or involved.
 * When thrown 500 http code is returned in final event response
 *
 * @author Albert Sikorski
 */
public class RouteExecutionException extends RuntimeException{
    public RouteExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
