package com.example.test;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;

import java.util.List;

public class SpringVpcStack extends Stack {

    public static void main(String[] args) {
        App app = new App(AppProps.builder().outdir("./cdk.out").build());
        new SpringVpcStack(app, "spring-vpc-stack", StackProps.builder().build());
        app.synth();
    }


    public SpringVpcStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        final List<SubnetConfiguration> subnets = List.of(SubnetConfiguration.builder()
                        .name("public")
                        .subnetType(SubnetType.PUBLIC)
                        .cidrMask(20)
                        .build(),
                SubnetConfiguration.builder()
                        .name("private")
                        .subnetType(SubnetType.PRIVATE_WITH_EGRESS)
                        .cidrMask(20)
                        .build());

        Vpc vpc = Vpc.Builder.create(this, "spring-vpc")
                .vpcName("spring-vpc")
                .maxAzs(3)
                .createInternetGateway(true)
                .enableDnsHostnames(true)
                .enableDnsSupport(true)
                .ipAddresses(IpAddresses.cidr("10.0.0.0/16"))
                .natGateways(1)
                .subnetConfiguration(subnets)
                .build();

        CfnOutput.Builder.create(this, "vpc-id")
                .value(vpc.getVpcId())
                .build();

        CfnOutput.Builder.create(this, "vpc-public-subnets")
                .value(vpc.getPublicSubnets()
                        .stream()
                        .map(ISubnet::getSubnetId)
                        .reduce((a, b) -> a + "," + b)
                        .orElse("")
                )
                .build();

        CfnOutput.Builder.create(this, "vpc-private-subnets")
                .value(vpc.getPrivateSubnets()
                        .stream()
                        .map(ISubnet::getSubnetId)
                        .reduce((a, b) -> a + "," + b)
                        .orElse("")
                )
                .build();
    }

}