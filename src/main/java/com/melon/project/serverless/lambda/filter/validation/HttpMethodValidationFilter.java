package com.melon.project.serverless.lambda.filter.validation;

import com.amazonaws.services.lambda.runtime.Context;
import com.melon.project.serverless.lambda.exception.RouteValidationException;
import com.melon.project.serverless.lambda.bind.RequestMethod;
import com.melon.project.serverless.lambda.events.HttpBasedEvent;
import com.melon.project.serverless.lambda.filter.Filter;
import org.springframework.util.StringUtils;

/**
 * Event validation - check HTTP Method
 *
 * @author Albert Sikorski
 */
public class HttpMethodValidationFilter implements Filter {
    @Override
    public Integer order() {
        return 1000;
    }

    @Override
    public void doFilter(HttpBasedEvent event, Context context) {
        boolean result;
        try {
            String method = event.getHttpMethod();
            result = StringUtils.hasLength(method);
            if(result){
                RequestMethod.valueOf(method);
            }
        }catch (IllegalArgumentException e){
            result = false;
        }

        if(!result){
            throw new RouteValidationException("Invalid HTTP method");
        }
    }
}
