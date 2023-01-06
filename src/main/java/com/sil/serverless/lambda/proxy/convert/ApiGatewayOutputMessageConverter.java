package com.sil.serverless.lambda.proxy.convert;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Output message converter for {@link APIGatewayV2HTTPResponse} based events
 *
 * @author Albert Sikorski
 *
 */
public class ApiGatewayOutputMessageConverter extends AbstractOutputMessageConverter<APIGatewayV2HTTPResponse> {
    private final static Logger logger = LoggerFactory.getLogger(ApiGatewayOutputMessageConverter.class);

    public ApiGatewayOutputMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    boolean isEventType(Class<?> parameterType) {
        return parameterType.equals(APIGatewayV2HTTPResponse.class);
    }

    @Override
    APIGatewayV2HTTPResponse withStatusCodeAndBody(int statusCode, String body) {
        logger.debug("Returning status code: {}, body: {}", statusCode, body);
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(statusCode)
                .withBody(body)
                .build();
    }
}
