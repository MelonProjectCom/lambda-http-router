package com.sil.serverless.lambda.filter.validation;

import com.amazonaws.services.lambda.runtime.Context;
import com.sil.serverless.lambda.bind.RequestMethod;
import com.sil.serverless.lambda.events.HttpBasedEvent;
import com.sil.serverless.lambda.exception.RouteValidationException;
import com.sil.serverless.lambda.filter.Filter;
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
