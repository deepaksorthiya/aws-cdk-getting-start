package com.example.test;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;

/**
 * Reasoning:
 * 1) Libraries used: aws-cdk-lib, constructs
 * 2) AWS resources impacted: VPC, Subnet, Internet Gateway, Route Table, Elastic IP, NAT Gateway
 * 3) AWS actions performed: Create VPC, Create Subnet, Create Internet Gateway, Create Route Table, Allocate Elastic IP,
 * Create NAT Gateway, Associate Subnet with Route Table, Add Route to Route Table for Internet access via NAT Gateway
 * 4) The code creates a VPC with a private subnet and configures internet access for the private subnet using a NAT Gateway.
 * It creates an Internet Gateway, attaches it to the VPC, creates a private route table and associates the private subnet with it. It then allocates an Elastic IP,
 * creates a NAT Gateway in the private subnet using the Elastic IP, and adds a route to the private route table to allow internet access via the NAT Gateway.
 */
public class SpringVpcStack extends Stack {
    public SpringVpcStack(final Construct parent, final String id) {
        super(parent, id);

        // Constants
        final String vpcCidrBlock = "10.0.0.0/16";
        final String privateSubnetCidrBlock = "10.0.144.0/20";
        final String availabilityZone = "ap-south-1b";
        final String vpcName = "spring-vpc-vpc";
        final String privateSubnetName = "spring-vpc-subnet-private2-ap-south-1b";
        final String internetGatewayName = "spring-vpc-igw";
        final String privateRouteTableName = "spring-vpc-rtb-private2-ap-south-1b";
        final String eipName = "spring-vpc-eip-ap-south-1a";
        final String natGatewayName = "spring-vpc-nat-public1-ap-south-1a";

        // Create VPC
        Vpc vpc = Vpc.Builder.create(this, vpcName)
                .cidrBlock(vpcCidrBlock)
                .instanceTenancy(DefaultInstanceTenancy.DEFAULT)
                .enableDnsHostnames(true)
                .build();

        // Create private subnet
        Subnet privateSubnet = Subnet.Builder.create(this, privateSubnetName)
                .vpcId(vpc.getVpcId())
                .cidrBlock(privateSubnetCidrBlock)
                .availabilityZone(availabilityZone)
                .build();

        // Create Internet Gateway
        InternetGateway internetGateway = InternetGateway.Builder.create(this, internetGatewayName)
                .vpc(vpc)
                .build();

        // Create private route table
        RouteTable privateRouteTable = RouteTable.Builder.create(this, privateRouteTableName)
                .vpc(vpc)
                .build();

        // Associate private subnet with private route table
        privateRouteTable.addPrivateSubnet(privateSubnet);

        // Allocate Elastic IP
        CfnEIP eip = CfnEIP.Builder.create(this, eipName)
                .domain("vpc")
                .build();

        // Create NAT Gateway
        CfnNatGateway natGateway = CfnNatGateway.Builder.create(this, natGatewayName)
                .allocationId(eip.getRef())
                .subnetId(privateSubnet.getSubnetId())
                .build();

        // Add route to private route table for internet access via NAT Gateway
        privateRouteTable.addGatewayEndpoint("NatGatewayRoute", GatewayVpcEndpointAwsService.NAT_GATEWAY, natGateway);
    }
}