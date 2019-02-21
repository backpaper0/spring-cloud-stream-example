package com.example;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SourceAppTest {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private MessageCollector messageCollector;
    @Autowired
    private Source source;

    @Test
    void testHandle() throws Exception {
        final Map<String, String> json = new HashMap<>();
        json.put("name", "hoge");
        final RequestEntity<Map<String, String>> request = RequestEntity.post(URI.create("/"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
        testRestTemplate.exchange(request, Void.class);

        final BlockingQueue<Message<?>> queue = messageCollector.forChannel(source.output());
        assertEquals(1, queue.size());
        final Message<?> message = queue.remove();
        assertEquals("{\"name\":\"hoge\"}", message.getPayload());
    }
}
