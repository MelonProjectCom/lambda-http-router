package com.melon.project.serverless.lambda.filter.validation;

import com.amazonaws.services.lambda.runtime.Context;
import com.melon.project.serverless.lambda.exception.RouteValidationException;
import com.melon.project.serverless.lambda.bind.RequestMethod;
import com.melon.project.serverless.lambda.events.HttpBasedEvent;
import com.melon.project.serverless.lambda.filter.Filter;
import org.springframework.util.StringUtils;

/**
 * Event validation - check if request body exist based on HTTP method
 *
 * @author Albert Sikorski
 */
public class EventStructureValidationFilter implements Filter {
    @Override
    public Integer order() {
        return 1002;
    }

    @Override
    public void doFilter(HttpBasedEvent event, Context context) {
        RequestMethod requestMethod = RequestMethod.valueOf(event.getHttpMethod());
        if((requestMethod == RequestMethod.GET)
        && StringUtils.hasLength(event.getBody())){
            throw new RouteValidationException("Invalid Event - body not allowed for method: " + requestMethod);
        }
    }
}
