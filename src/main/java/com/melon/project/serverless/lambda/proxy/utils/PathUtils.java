package com.melon.project.serverless.lambda.proxy.utils;

import org.springframework.util.StringUtils;

/**
 * Path Utils class
 *
 * @author Albert Sikorski
 */
public class PathUtils {

    public static String getPath(String classLevelPath, String methodLevelPath){
        StringBuilder sb = new StringBuilder();

        String classLevelPathWithoutSlashes = removeSlashes(classLevelPath);
        if(StringUtils.hasLength(classLevelPathWithoutSlashes)){
            sb.append("/").append(classLevelPathWithoutSlashes);
        }
        String methodLevelPathWithoutSlashes = removeSlashes(methodLevelPath);
        if(StringUtils.hasLength(methodLevelPathWithoutSlashes)){
            sb.append("/").append(methodLevelPathWithoutSlashes);
        }

        return sb.toString();
    }

    private static String removeSlashes(String str){
        if(StringUtils.hasLength(str)){
            if(str.startsWith("/")){
                str = str.substring(1);
            }
            if(str.endsWith("/")){
                str = str.substring(0, str.length() - 1);
            }
        }
        return str;
    }
}
