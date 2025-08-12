package com.example.hellostack;

import org.junit.jupiter.api.Test;
import software.amazon.awscdk.App;
import software.amazon.awscdk.assertions.Template;

import java.io.IOException;
import java.util.HashMap;

class HelloCdkStackTest {

    @Test
    public void testStack() throws IOException {
        App app = new App();
        HelloCdkStack stack = new HelloCdkStack(app, "test");

        Template template = Template.fromStack(stack);

        template.hasResourceProperties("AWS::SQS::Queue", new HashMap<String, Number>() {{
            put("VisibilityTimeout", 300);
        }});
    }
}
