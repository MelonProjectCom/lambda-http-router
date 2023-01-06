package com.sil.serverless.lambda.proxy;

import com.sil.serverless.lambda.bind.RequestMethod;

/**
 * Holds route details HTTP method and path
 *
 * @author Albert Sikorski
 *
 */
public record MethodPath(RequestMethod requestMethod, String path) {
}
