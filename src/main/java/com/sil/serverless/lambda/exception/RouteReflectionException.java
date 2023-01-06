package com.sil.serverless.lambda.exception;

/**
 * RouteReflectionException can be thrown when route method can't be accessed by reflection.
 * When thrown 500 http code is returned in final event response
 *
 * @author Albert Sikorski
 */
public class RouteReflectionException extends RuntimeException{
    public RouteReflectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
