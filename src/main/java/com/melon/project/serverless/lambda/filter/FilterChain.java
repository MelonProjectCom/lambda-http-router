package com.melon.project.serverless.lambda.filter;

import com.amazonaws.services.lambda.runtime.Context;
import com.melon.project.serverless.lambda.events.HttpBasedEvent;

/**
 * Interface used to process all filters from application context
 *
 * @author Albert Sikorski
 */
public interface FilterChain {

    void doFilter(HttpBasedEvent event, Context context);
}
