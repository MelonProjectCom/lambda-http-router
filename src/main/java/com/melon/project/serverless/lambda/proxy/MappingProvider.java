package com.melon.project.serverless.lambda.proxy;

import com.melon.project.serverless.lambda.bind.annotation.LambdaController;
import com.melon.project.serverless.lambda.bind.annotation.PathMapping;
import com.melon.project.serverless.lambda.exception.RouteExecutionException;
import com.melon.project.serverless.lambda.exception.RoutePreparationException;
import com.melon.project.serverless.lambda.exception.RouteReflectionException;
import com.melon.project.serverless.lambda.proxy.convert.InputMessageConverter;
import com.melon.project.serverless.lambda.proxy.convert.OutputMessageConverter;
import com.melon.project.serverless.lambda.proxy.convert.ParameterDetails;
import com.melon.project.serverless.lambda.proxy.utils.AnnotationUtils;
import com.melon.project.serverless.lambda.proxy.utils.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Analise all {@link LambdaController } annotated classes/beans to find user defined mappings.
 * As result produce {@code  MAP<UserMapping, UserMethodReference> }
 *
 * @author Albert Sikorski
 *
 */
public class MappingProvider<T,R>{
    private final static Logger logger = LoggerFactory.getLogger(MappingProvider.class);

    private final ApplicationContext applicationContext;
    private final InputMessageConverter<T> inputMessageConverter;
    private final OutputMessageConverter<R> outputMessageConverter;
    private final Class<T> inputObjectType;
    private final Class<R> outputObjectType;

    private Map<MethodPath, Function<T, R>> mapping = null;

    public MappingProvider(Class<T> inputObjectType, Class<R> outputObjectType, ApplicationContext applicationContext,
                           InputMessageConverter<T> inputMessageConverter, OutputMessageConverter<R> outputMessageConverter){
        this.inputObjectType = inputObjectType;
        this.outputObjectType = outputObjectType;
        this.applicationContext = applicationContext;
        this.inputMessageConverter = inputMessageConverter;
        this.outputMessageConverter = outputMessageConverter;
    }

    protected Map<MethodPath, Function<T, R>> mappings() {
        if(mapping == null){
            try {
                mapping = Stream.of(getMappingForClassLevelFunctions(), getMappingToMethods())
                        .flatMap(map -> map.entrySet().stream())
                        .collect(Collectors.toUnmodifiableMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ));

                if(logger.isDebugEnabled()){
                    logger.debug("Found {} mappings", mapping.size());
                    mapping.forEach(((methodPath, beanToFunctionMapping) -> logger.debug("Found mapping for {} {}", methodPath.requestMethod(), methodPath.path())));
                }
            }catch (IllegalStateException e){
                logger.debug("Could not prepare mappings... Reason: {}", e.getMessage(), e);
                throw new RoutePreparationException(e.getMessage(), e);
            }
        }
        return mapping;
    }

    private Map<MethodPath, Function<T, R>> getMappingForClassLevelFunctions() {
        String[] beanNamesForType = applicationContext.getBeanNamesForType(
                ResolvableType.forClassWithGenerics(Function.class, inputObjectType, outputObjectType));
        return Arrays.stream(beanNamesForType)
                .map(beanName -> (Function<T, R>) applicationContext.getBean(beanName))
                .filter(bean -> bean.getClass().isAnnotationPresent(PathMapping.class))
                .collect(Collectors.toMap(
                        bean -> new MethodPath(
                                bean.getClass().getAnnotation(PathMapping.class).method(),
                                PathUtils.getPath(AnnotationUtils.getClassLevelPath(bean.getClass()), "")),
                        bean -> bean
                ));
    }

    private Map<MethodPath, Function<T, R>> getMappingToMethods() {
        Map<MethodPath, Function<T, R>> mapping = new HashMap<>();

        getLambdaControllerBeans().values().forEach( bean -> {
            final String classPath = AnnotationUtils.getClassLevelPath(bean.getClass());
            mapping.putAll(getAnnotatedFunctions(bean.getClass(), classPath));
            mapping.putAll(getAnnotatedMethods(bean.getClass(), classPath));
        });

        return mapping;
    }
    private Map<MethodPath, Function<T,R>> getAnnotatedFunctions(Class<?> clazz, String classPath) {
        if(Function.class.isAssignableFrom(clazz)){
            return Collections.emptyMap();
        }
        return Arrays.stream(clazz.getMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .filter(method -> method.getReturnType().equals(Function.class))
                .filter(this::checkIfFunctionReturnType)
                .map(AnnotationUtils::getMethodAndPath)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(
                        methodMapping -> new MethodPath(methodMapping.requestMethod(), PathUtils.getPath(classPath, methodMapping.path())),
                        methodMapping -> toFunction(methodMapping.method(), clazz)
                ));
    }

    private Map<MethodPath, Function<T,R>> getAnnotatedMethods(Class<?> clazz, String classPath) {
        if(Function.class.isAssignableFrom(clazz)){
            return Collections.emptyMap();
        }
        return Arrays.stream(clazz.getMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .filter(method -> !method.getReturnType().equals(Function.class))
                .map(AnnotationUtils::getMethodAndPath)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(
                        methodMapping -> new MethodPath(methodMapping.requestMethod(), PathUtils.getPath(classPath, methodMapping.path())),
                        methodMapping -> toFunctionFromMethod(methodMapping.method(), clazz, PathUtils.getPath(classPath, methodMapping.path()))
                ));
    }

    private Function<T, R> toFunction(Method method, Class<?> clazz){
        try {
            return (Function<T, R>) method.invoke(applicationContext.getBean(clazz));
        } catch (IllegalAccessException e) {
            //It shouldn't happen - accessibility already checked before invocation
            throw new RoutePreparationException("Could not access method " + method.getName(), e);
        } catch (InvocationTargetException e) {
            throw new RoutePreparationException(e.getMessage(), e);
        }
    }

    private Function<T, R> toFunctionFromMethod(Method method, Class<?> clazz, String pathTemplate){

        List<ParameterDetails> inputs = inputMessageConverter.getInputParameters(method);
        logger.trace("Found {} inputs", inputs.size());
        return (event) -> {
            try {
                return outputMessageConverter.output(
                        method.invoke(
                                applicationContext.getBean(clazz),
                                inputMessageConverter.getInputVales(event, inputs, pathTemplate)
                        ),
                        method.getReturnType()
                );
            } catch (IllegalAccessException e) {
                throw new RouteReflectionException("Could not access method " + method.getName(), e);
            } catch (InvocationTargetException e) {
                throw new RouteExecutionException(e.getCause().getMessage(), e);
            }
        };
    }

    private boolean checkIfFunctionReturnType(Method method){
        Type[] actualTypeArguments = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments();
        return actualTypeArguments.length == 2 && inputObjectType.equals(actualTypeArguments[0])
                && outputObjectType.equals(actualTypeArguments[1]);
    }

    private Map<String, Object> getLambdaControllerBeans(){
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(LambdaController.class);
        logger.trace("Found {} LambdaController beans", beansWithAnnotation.size());
        return beansWithAnnotation;
    }
}
