# aws-lambda-router


> **INFO**: Because of missing official support for Java 17 in AWS Lambda there are two options 
> to use this library:
> - Native image
> - Build your own environment with java 17 inside

# Project idea

Simplify development process for Spring boot projects. 
This framework allows to expose multiple endpoints in single AWS Lambda. 

It is design similar to well known Spring boot web approach where using few annotations you can easily expose new handler.

Project goals are:
 - A faster way to deliver ready-to-use backend solutions
 - Simplify development process
 - Reduce the amount of code 

# Getting started

> **Examples**: https://github.com/MelonProjectCom/lambda-http-router-examples

```java
import com.melon.project.serverless.lambda.bind.annotation.Body;
import com.melon.project.serverless.lambda.bind.annotation.LambdaController;
import com.melon.project.serverless.lambda.bind.annotation.PathPostMapping;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@LambdaController
@SpringBootApplication
public class Example {

    public static void main(String[] args) {
        SpringApplication.run(Example.class, args);
    }

    @PathPostMapping("/example/test")
    public String example(@Body String body) {
        return "Hello World! - body: " + body;
    }
}

```
## Configuration

### API Gateway

Set property (Optional)
```yaml
lambda:
  router:
    mode: API_GATEWAY
```

Handler Configuration - AWS Lambda function:
```markdown
com.melon.project.serverless.lambda.entrypoint.ApiGatewayRequestHandler::handleRequest
```

### Application Load Balancer

Set property
```yaml
lambda:
  router:
    mode: ALB
```

Handler Configuration - AWS Lambda function:
```markdown
com.melon.project.serverless.lambda.entrypoint.AlbRequestHandler::handleRequest
```

# Pros and restrictions

## Pros
 - Simplifies infrastructure - single/few lambda functions for all endpoints instead of single per each endpoint
 - Simplifies development process - single code/repository for many endpoints
 - Reducing the cost - in case of provisioned lambda instances (provisioned concurrency) we pay only for single function
 - Reducing the number of cold starts and long responses
 - Graalvm and Spring native support - the ability to compile to a native image


## Restrictions
It is design to use lambda behind:
 - AWS Api Gateway
 - AWS Application Load Balancer

### Requirements
 - Java 17+
 - Spring Boot 3+

# License
