package com.sil.serverless.lambda.exception;

/**
 * RoutePreparationException can be thrown during route preparation - application startup
 *
 * @author Albert Sikorski
 */
public class RoutePreparationException extends RuntimeException{

    public RoutePreparationException(String message) {
        super(message);
    }
    public RoutePreparationException(String message, Throwable cause) {
        super(message, cause);
    }

}
