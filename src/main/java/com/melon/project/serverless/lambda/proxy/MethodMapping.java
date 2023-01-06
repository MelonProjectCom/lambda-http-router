package com.melon.project.serverless.lambda.proxy;

import com.melon.project.serverless.lambda.bind.RequestMethod;

import java.lang.reflect.Method;

/**
 * Holds method mapping details
 *
 * @author Albert Sikorski
 *
 */
public record MethodMapping(Method method, RequestMethod requestMethod, String path) {
}
