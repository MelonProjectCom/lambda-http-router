package com.sil.serverless.lambda.proxy;

import com.amazonaws.services.lambda.runtime.Context;
import com.sil.serverless.lambda.bind.RequestMethod;
import com.sil.serverless.lambda.events.HttpBasedEvent;
import com.sil.serverless.lambda.exception.RouteAccessException;
import com.sil.serverless.lambda.exception.RouteExecutionException;
import com.sil.serverless.lambda.exception.RouteReflectionException;
import com.sil.serverless.lambda.exception.RouteValidationException;
import com.sil.serverless.lambda.filter.FilterChain;
import com.sil.serverless.lambda.proxy.context.StaticLambdaContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Distributes events to user methods based on HTTP Method and request path.
 * Wraps method response to final lambda response type
 *
 * @author Albert Sikorski
 *
 */
public abstract class AbstractMappingProxy<T extends HttpBasedEvent, R> implements MappingProxy<T, R> {

    private final static Logger logger = LoggerFactory.getLogger(AbstractMappingProxy.class);

    private final Map<MethodPath, Function<T, R>> mapping;
    private final MappingMatcher mappingMatcher;

    private final FilterChain filterChain;

    protected AbstractMappingProxy(Map<MethodPath, Function<T, R>> mapping, MappingMatcher mappingMatcher, FilterChain filterChain) {
        this.mapping = mapping;
        this.mappingMatcher = mappingMatcher;
        this.filterChain = filterChain;
    }

    public R proxy(T inputEvent, Context context){
        logger.trace("Input event value: {}", inputEvent);
        try{
            filterChain.doFilter(inputEvent, context);

            StaticLambdaContext.setContext(context);

            return findMapping(
                    RequestMethod.valueOf(inputEvent.getHttpMethod()),
                    inputEvent.getPath()
            ).orElse(missingMappingFunction()).apply(inputEvent);
        }catch (RouteValidationException e){
            return withCodeAndBodyResponse(400, e.getMessage());
        }catch (RouteExecutionException e){
            logger.error("Unhandled exception in Lambda controller", e);
            return withCodeAndBodyResponse(500, "Internal server error");
        }catch (RouteReflectionException e){
            logger.error("Could not access user method", e);
            return withCodeAndBodyResponse(500, "Internal server error");
        }catch (RouteAccessException e){
            logger.debug("Invalid access exception. Reason: {}", e.getMessage(), e);
            return withCodeAndBodyResponse(e.getHttpCode(), e.getMessage());
        }
    }

    protected abstract R withCodeAndBodyResponse(int statusCode, String body);

    private Optional<Function<T,R>> findMapping(RequestMethod requestMethod, String eventPath) {
        logger.debug("Searching for mapping - Event path: {}", eventPath);
        Optional<MethodPath> match = mappingMatcher.getMatch(requestMethod, eventPath);
        logger.debug("Found mapping: {}", match);
        Optional<Function<T, R>> mappingFunction = match.map(mapping::get);
        logger.debug("Found matching function: {}", mappingFunction);
        return mappingFunction;
    }

    private Function<T,R> missingMappingFunction(){

        return event -> {
            logger.debug("Missing mapping - fallback");
            return withCodeAndBodyResponse(404, "Not found");
        };
    }
}
