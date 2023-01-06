package com.melon.project.serverless.lambda.proxy;

import com.melon.project.serverless.lambda.bind.RequestMethod;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides matching method to find user defined path mapping
 *
 * @author Albert Sikorski
 *
 */
public class MappingMatcher {

    private final Set<MethodPath> methodPaths;
    private final Map<MethodPath, List<String>>  methodPathMapping;
    public MappingMatcher(Set<MethodPath> methodPaths){
        this.methodPaths = methodPaths;
        this.methodPathMapping = prepareMappings(methodPaths);
    }

    public Optional<MethodPath> getMatch(RequestMethod requestMethod, String requestPath) {
        List<String> requestPathList = Arrays.asList(requestPath.split("/"));

        for (MethodPath methodPath : methodPaths) {
            if(isMatching(methodPath, requestMethod, requestPathList)){
                return Optional.of(methodPath);
            }
        }
        return Optional.empty();
    }

    private boolean isMatching(MethodPath methodPath, RequestMethod requestMethod, List<String> requestPathList) {
        if(methodPath.requestMethod().equals(requestMethod)){
            return matchPath(methodPathMapping.get(methodPath), requestPathList);
        }
        return false;
    }

    private static boolean matchPath(List<String> routePathList, List<String> requestPathList) {
        if(routePathList.size() == requestPathList.size()){
            Iterator<String> routePathIterator = routePathList.iterator();
            Iterator<String> requestPathIterator = requestPathList.iterator();
            while (routePathIterator.hasNext()) {
                String routePathSection = routePathIterator.next();
                String requestPathSection = requestPathIterator.next();
                if (!routePathSection.equals(requestPathSection) && !routePathSection.equals("*")) {
                    // as soon as a difference is found, stop looping
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private Map<MethodPath, List<String>> prepareMappings(Set<MethodPath> methodPaths){
        return methodPaths.stream().collect(Collectors.toMap(
                methodPath -> methodPath,
                methodPath -> Arrays.stream(methodPath.path().split("/"))
                        .map(pathSection -> pathSection.startsWith("{") && pathSection.endsWith("}")? "*" : pathSection)
                        .toList()
                ));
    }
}
