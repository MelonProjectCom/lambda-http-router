package com.melon.project.serverless.lambda.filter;

import com.amazonaws.services.lambda.runtime.Context;
import com.melon.project.serverless.lambda.events.HttpBasedEvent;

/**
 * Interface allows to validate and transform input event object before mapping to final handler
 *
 * @author Albert Sikorski
 */
public interface Filter {
    Integer order();
    void doFilter(HttpBasedEvent event, Context context);
}
