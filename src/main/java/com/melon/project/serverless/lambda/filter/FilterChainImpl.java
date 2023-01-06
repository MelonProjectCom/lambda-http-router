package com.melon.project.serverless.lambda.filter;

import com.amazonaws.services.lambda.runtime.Context;
import com.melon.project.serverless.lambda.events.HttpBasedEvent;

import java.util.Comparator;
import java.util.List;

/**
 * Default implementation of FilterChain interface.
 * Sorts filers based on order field and execute all filters every time doFilter() method is called
 *
 * @author Albert Sikorski
 */
public class FilterChainImpl implements FilterChain{

    private final List<Filter> filters;

    public FilterChainImpl(List<Filter> filterList) {
        filterList.sort(Comparator.comparing(Filter::order));
        filters = filterList;
    }

    @Override
    public void doFilter(HttpBasedEvent event, Context context) {
        filters.forEach(filter -> filter.doFilter(event, context));
    }
}
