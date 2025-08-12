package com.example.test;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.ProxyResourceOptions;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;

public class AwsLamdaGatewayRest extends Stack {

    public static void main(final String[] args) {
        App app = new App(AppProps.builder().outdir("./cdk.out").build());
        new AwsLamdaGatewayRest(app, "AwsLamdaGatewayRest", StackProps.builder()
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

        // Lambda function (Spring Boot fat JAR)
        Function springLambda = Function.Builder.create(this, "SpringLambda")
                .runtime(Runtime.JAVA_21)
                .handler("com.example.LambdaHandler::handleRequest")
                .memorySize(1024)
                .timeout(Duration.seconds(30))
                .code(Code.fromAsset("target/aws-cdk-getting-start-1.0.0-SNAPSHOT.jar")) // Path to your
                // built JAR
                .build();

        // API Gateway REST API
        RestApi api = RestApi.Builder.create(this, "SpringApi")
                .restApiName("Spring Lambda API")
                .description("API Gateway with Spring Boot Lambda integration")
                .build();

        // Add /api resource
        var apiResource = api.getRoot().addResource("api");

        // Proxy all requests under /api to Lambda
        LambdaIntegration lambdaIntegration = LambdaIntegration.Builder.create(springLambda).build();
        apiResource
                .addProxy(ProxyResourceOptions.builder().defaultIntegration(lambdaIntegration).anyMethod(true).build());

    }

}
