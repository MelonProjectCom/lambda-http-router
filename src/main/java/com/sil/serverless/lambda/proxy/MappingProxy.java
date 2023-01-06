package com.sil.serverless.lambda.proxy;

import com.amazonaws.services.lambda.runtime.Context;
import com.sil.serverless.lambda.events.HttpBasedEvent;

/**
 * Provides single method to handle routing to correct user method.
 *
 * @author Albert Sikorski
 *
 */
public interface MappingProxy<T extends HttpBasedEvent,R> {
    R proxy(T inputEvent, Context context);

}
