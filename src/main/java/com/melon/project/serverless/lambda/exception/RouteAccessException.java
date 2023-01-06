package com.melon.project.serverless.lambda.exception;

import com.melon.project.serverless.lambda.filter.Filter;

/**
 * RouteAccessException can be thrown in Filter.
 * Code and message will be used in final event response
 *
 * @see Filter
 * @author Albert Sikorski
 */
public class RouteAccessException extends RuntimeException{

    private final int httpCode;

    public enum ResponseCode{
        UNAUTHORIZED(401),
        FORBIDDEN(403);

        private final int code;
        ResponseCode(int code){
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public RouteAccessException(ResponseCode responseCode, String message) {
        super(message);
        this.httpCode = responseCode.getCode();
    }

    public int getHttpCode() {
        return httpCode;
    }
}
