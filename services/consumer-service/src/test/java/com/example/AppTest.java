package com.example;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.messaging.support.MessageBuilder;

@SpringBootTest
@EnableTestBinder
@ExtendWith(OutputCaptureExtension.class)
public class AppTest {

    @Autowired
    private InputDestination input;

    @Test
    void testHandle(CapturedOutput output) {
        var payload = "{\"name\":\"hoge\"}";
        var message = MessageBuilder.withPayload(payload).build();
        input.send(message);

        assertThat(output.getOut()).contains("MQ -> consumer-service: " + payload);
    }
}
