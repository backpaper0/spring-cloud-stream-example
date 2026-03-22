package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest
@EnableTestBinder
@AutoConfigureRestTestClient
public class AppTest {

    @Autowired
    private OutputDestination output;

    @Autowired
    private RestTestClient rest;

    @Test
    void testHandle() throws Exception {
        var payload = "{\"content\":\"hoge\"}";
        rest.post()
                .uri("http://localhost:8080/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .exchangeSuccessfully();

        Message<byte[]> message = output.receive();
        assertEquals(payload, new String(message.getPayload()));
    }
}
