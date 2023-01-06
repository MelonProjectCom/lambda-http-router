package com.melon.project.serverless.lambda.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Utils class used to find Spring boot start-class.
 * It's required when code is executed in Lambda environment.
 * It is possible to set Main class name as environment variable if not it will try to find it in jars
 *
 * @author Albert Sikorski
 *
 */
public class LambdaApplicationUtils {
    private final static Logger logger = LoggerFactory.getLogger(LambdaApplicationUtils.class);
    private final static String MAIN_CLASS_ENV = "MAIN_CLASS";


    public static Class<?> getStartClass() throws IllegalArgumentException {
        ClassLoader classLoader = LambdaApplicationUtils.class.getClassLoader();
        return getMainClass(classLoader);
    }

    static Class<?> getMainClass(ClassLoader classLoader) throws IllegalArgumentException {

        Class<?> startClass = null;
        if (StringUtils.hasLength(System.getenv(MAIN_CLASS_ENV))) {
            startClass = ClassUtils.resolveClassName(System.getenv(MAIN_CLASS_ENV), classLoader);
        } else if (StringUtils.hasLength(System.getProperty(MAIN_CLASS_ENV))) {
            startClass = ClassUtils.resolveClassName(System.getProperty(MAIN_CLASS_ENV), classLoader);
        }else {
            logger.debug("Main class not found in environment variables and properties. " +
                    "Searching in Manifest...");
            try {
                startClass = getStartClass(
                        Collections.list(classLoader.getResources(JarFile.MANIFEST_NAME)), classLoader);
            } catch (IOException e) {
                logger.debug("Could not load manifests...");
            }
        }

        if(startClass == null){
            logger.debug("Could not find and load main class");
            throw new IllegalArgumentException("Could not find and load main class");
        }else {
            logger.debug("Found Start class: {}", startClass.getName());
            return startClass;
        }
    }

    private static Class<?> getStartClass(List<URL> list, ClassLoader classLoader)  {
        for (URL url : list) {
            try {
                var manifest = new Manifest(url.openStream());
                logger.debug("Searching start class in manifest: {}", url);
                var mainAttributes = manifest.getMainAttributes();
                String startClassName = mainAttributes.getValue("Start-Class");
                if (!StringUtils.hasLength(startClassName)) {
                    startClassName = mainAttributes.getValue("Main-Class");
                }
                if (StringUtils.hasLength(startClassName)) {
                    Class<?> startClass = ClassUtils.forName(startClassName, classLoader);
                    if(isSpringBootApplication(startClass)){
                        logger.debug("Found start (Spring boot) class - {}", startClassName);
                        return startClass;
                    }
                }
            }catch (IOException e){
                logger.debug("Failed to open manifest URL: {}", url);
            } catch (ClassNotFoundException e) {
                logger.debug("Could not find start/main class in classpath", e);
            }
        }
        return null;
    }

    private static boolean isSpringBootApplication(Class<?> startClass) {
        return startClass.isAnnotationPresent(SpringBootApplication.class)
                || startClass.isAnnotationPresent(SpringBootConfiguration.class);
    }
}
