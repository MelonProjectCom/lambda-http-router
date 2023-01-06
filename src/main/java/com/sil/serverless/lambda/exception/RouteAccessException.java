package com.sil.serverless.lambda.exception;

/**
 * RouteAccessException can be thrown in Filter.
 * Code and message will be used in final event response
 *
 * @see com.sil.serverless.lambda.filter.Filter
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
