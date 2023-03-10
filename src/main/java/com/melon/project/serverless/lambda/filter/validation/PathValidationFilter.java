package com.melon.project.serverless.lambda.filter.validation;

import com.amazonaws.services.lambda.runtime.Context;
import com.melon.project.serverless.lambda.exception.RouteValidationException;
import com.melon.project.serverless.lambda.events.HttpBasedEvent;
import com.melon.project.serverless.lambda.filter.Filter;
import org.springframework.util.StringUtils;

/**
 * Event validation - check path
 *
 * @author Albert Sikorski
 */
public class PathValidationFilter implements Filter {
    @Override
    public Integer order() {
        return 1001;
    }

    @Override
    public void doFilter(HttpBasedEvent event, Context context) {
        String eventPath = event.getPath();
        if(!(StringUtils.hasLength(eventPath) && eventPath.startsWith("/"))){
            throw new RouteValidationException("Invalid Path");
        }
    }
}
