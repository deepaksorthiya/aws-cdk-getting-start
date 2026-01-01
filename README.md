# Welcome to your CDK Java project!

This is a blank project for CDK development with Java.

The `cdk.json` file tells the CDK Toolkit how to execute your app.

It is a [Maven](https://maven.apache.org/) based project, so you can open this project with any Maven compatible Java
IDE to build and run tests.

## Useful commands

for localstack

* `mvn clean package`     compile and run tests
* `mvn exec:java` for running main class configured in pom.xml
* `cdklocal ls`          list all stacks in the app
* `cdklocal synth`       emits the synthesized CloudFormation template
* `cdklocal bootstrap`   bootstrap env
* `cdklocal deploy --require-approval never`      deploy this stack to your default AWS account/region
* `cdklocal destroy --require-approval never`      destroy stack
* `cdklocal diff`        compare deployed stack with current state
* `cdklocal synth > template.yaml`    create template.yaml file
* `cdklocal docs`        open CDK documentation

for aws cdk

* `mvn clean package`     compile and run tests
* `mvn exec:java` for running main class configured in pom.xml
* `cdk ls`          list all stacks in the app
* `cdk synth`       emits the synthesized CloudFormation template
* `cdk bootstrap`   bootstrap env
* `cdk deploy --require-approval never`      deploy this stack to your default AWS account/region
* `cdk destroy --require-approval never`      destroy stack
* `cdk diff`        compare deployed stack with current state
* `cdk synth > template.yaml`    create template.yaml file
* `cdk docs`        open CDK documentation

Enjoy!
