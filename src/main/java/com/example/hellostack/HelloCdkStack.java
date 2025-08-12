package com.example.hellostack;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;

public class HelloCdkStack extends Stack {
    public static void main(final String[] args) {
        App app = new App(AppProps.builder().outdir("./cdk.out").build());
        new HelloCdkStack(app, "HelloCdkStack", StackProps.builder()
                // If you don't specify 'env', this stack will be environment-agnostic.
                // Account/Region-dependent features and context lookups will not work,
                // but a single synthesized template can be deployed anywhere.

                // Uncomment the next block to specialize this stack for the AWS Account
                // and Region that are implied by the current CLI configuration.
                /*
                .env(Environment.builder()
                        .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                        .region(System.getenv("CDK_DEFAULT_REGION"))
                        .build())
                */

                // Uncomment the next block if you know exactly what Account and Region you
                // want to deploy the stack to.
                /*
                .env(Environment.builder()
                        .account("123456789012")
                        .region("us-east-1")
                        .build())
                */

                // For more information, see https://docs.aws.amazon.com/cdk/latest/guide/environments.html
                .build());

        app.synth();
    }

    public HelloCdkStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public HelloCdkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // The code that defines your stack goes here

        // Queue resource
        final Queue queue = Queue.Builder.create(this, "HelloCdkQueue")
                .visibilityTimeout(Duration.seconds(300))
                .build();
    }
}

