package com.sil.serverless.lambda.filter;

import com.amazonaws.services.lambda.runtime.Context;
import com.sil.serverless.lambda.events.HttpBasedEvent;

/**
 * Interface used to process all filters from application context
 *
 * @author Albert Sikorski
 */
public interface FilterChain {

    void doFilter(HttpBasedEvent event, Context context);
}
