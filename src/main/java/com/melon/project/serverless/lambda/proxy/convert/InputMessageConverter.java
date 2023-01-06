package com.melon.project.serverless.lambda.proxy.convert;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Provides methods to map Input event object to user mapping method parameters
 *
 * @author Albert Sikorski
 *
 */
public interface InputMessageConverter<T> {
    List<ParameterDetails> getInputParameters(Method parameters);

    Object[] getInputVales(T event, List<ParameterDetails> inputs, String pathTemplate);
}
