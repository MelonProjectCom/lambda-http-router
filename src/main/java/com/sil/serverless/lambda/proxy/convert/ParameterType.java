package com.sil.serverless.lambda.proxy.convert;

/**
 * Supported mapping parameter types
 *
 * @author Albert Sikorski
 *
 */
public enum ParameterType {
    WHOLE_INPUT,
    BODY,
    HEADERS,
    HEADER,
    PATH_PARAM,
    QUERY_PARAM,
    UNKNOWN
}
