package com.sil.serverless.lambda.filter.validation;

import com.amazonaws.services.lambda.runtime.Context;
import com.sil.serverless.lambda.bind.RequestMethod;
import com.sil.serverless.lambda.events.HttpBasedEvent;
import com.sil.serverless.lambda.exception.RouteValidationException;
import com.sil.serverless.lambda.filter.Filter;
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
