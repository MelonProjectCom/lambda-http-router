package com.melon.project.serverless.lambda.proxy.convert;

/**
 * Provides method to map Output event object to user method return type
 *
 * @author Albert Sikorski
 *
 */
public interface OutputMessageConverter<T> {
    T output(Object event, Class<?> type);
}
