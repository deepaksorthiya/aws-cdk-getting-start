package com.example.test;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.apigateway.IResource;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

public class AwsLamdaGatewayRest extends Stack {

    public static void main(final String[] args) {
        App app = new App(AppProps.builder().outdir("./cdk.out").build());
        new AwsLamdaGatewayRest(app, "AwsSpringBootLambdaGatewayRest", StackProps.builder()
                // If you don't specify 'env', this stack will be environment-agnostic.
                // Account/Region-dependent features and context lookups will not work,
                // but a single synthesized template can be deployed anywhere.

                // Uncomment the next block to specialize this stack for the AWS Account
                // and Region that are implied by the current CLI configuration.
                /*
                 * .env(Environment.builder() .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                 * .region(System.getenv("CDK_DEFAULT_REGION")) .build())
                 */

                // Uncomment the next block if you know exactly what Account and Region you
                // want to deploy the stack to.
                /*
                 * .env(Environment.builder() .account("123456789012") .region("us-east-1")
                 * .build())
                 */

                // For more information, see
                // https://docs.aws.amazon.com/cdk/latest/guide/environments.html
                .build());

        app.synth();
    }

    public AwsLamdaGatewayRest(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsLamdaGatewayRest(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // The code that defines your stack goes here

        // This is a placeholder path to your Spring Boot application's JAR file.
        // The `cdk.out` directory is the default output for the CDK.
        // We will be building our Spring Boot app to a JAR file and referencing it here.
        final String handlerPath = "E:\\worksspaces\\aws-localstack\\aws\\aws-lamda\\aws-lamda-spring\\target\\aws-lamda-spring-1.0.0-SNAPSHOT-lambda-package.zip";

        // Create the Lambda function.
        // We use Runtime.JAVA_21 which is the latest LTS version supported by AWS.
        // The handler is set to the Spring Cloud Function entry point.
        final Function apiLambda = Function.Builder.create(this, "SpringLambda")
                .runtime(Runtime.JAVA_21)
                .handler("com.example.StreamLambdaHandler::handleRequest")
                .memorySize(1024)
                .timeout(Duration.seconds(30))
                .code(Code.fromAsset(handlerPath))
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
