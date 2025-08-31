package com.example.test;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.apigateway.IResource;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.Map;

public class SpringBootJavaGraalVMStack extends Stack {

    public static void main(final String[] args) {
        App app = new App(AppProps.builder().outdir("./cdk.out").build());
        new SpringBootJavaGraalVMStack(app, "AwsSpringBootJavaGraalVMStackLambdaGatewayRest", StackProps.builder()
                .build());
        app.synth();
    }

    public SpringBootJavaGraalVMStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        String codePath = "E:\\worksspaces\\aws-localstack\\code-examples\\aws-lambda-java-workshop\\labs\\unicorn-store\\software\\alternatives\\unicorn-store-spring-graalvm\\lambda-spring-graalvm.zip";
        final Function apiLambda = Function.Builder.create(this, "UnicornStoreSpringGraalVMFunction")
                .runtime(Runtime.PROVIDED_AL2023)
                .functionName("unicorn-store-spring-graalvm")
                .memorySize(2048)
                .timeout(Duration.seconds(29))
                .code(Code.fromAsset(codePath))
                .handler("com.amazonaws.serverless.proxy.spring.SpringDelegatingLambdaContainerHandler")
                .environment(Map.of(
                        "MAIN_CLASS", "com.unicorn.store.StoreApplication")
                )
                .build();

        // Create the REST API gateway and deployment stage
        final RestApi api =
                RestApi.Builder.create(this, "SpringRestApi")
                        .restApiName("Spring Serverless API")
                        .description("This API is a serverless backend for a Spring Boot application.")
                        .deployOptions(StageOptions.builder()
                                .stageName("dev")
                                .build())
                        .build();

        // Create an integration between the Lambda function and the API Gateway.
        // This will be a proxy integration, meaning all requests and responses
        // are passed directly between the API Gateway and Lambda without any
        // mapping templates.
        final LambdaIntegration lambdaIntegration = LambdaIntegration.Builder
                .create(apiLambda)
                .build();

        // Define a root resource for the API (e.g., '/hello').
        final IResource apiRoot = api.getRoot();

        // Map the root path of the API Gateway to the Lambda.
        // This handles requests to the base URL, e.g., https://<api-id>.execute-api.<region>.amazonaws.com/dev
        api.getRoot().addMethod("ANY", lambdaIntegration);

        // Add a proxy resource '{proxy+}', which captures all paths under the root.
        // This is a common pattern for proxying all requests to a single backend.
        // Add a catch-all method (ANY) to the proxy resource.
        // The 'ANY' method handles all HTTP verbs (GET, POST, PUT, DELETE, etc.).
        apiRoot
                .addProxy(ProxyResourceOptions.builder()
                        .defaultIntegration(lambdaIntegration)
                        .anyMethod(true).
                        build());

    }

}
