package com.example;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.messaging.Message;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(TestChannelBinderConfiguration.class)
public class SourceAppTest {

	@Autowired
	private TestRestTemplate testRestTemplate;
	@Autowired
	private OutputDestination output;

	@Test
	void testHandle() throws Exception {
		final Map<String, String> json = new HashMap<>();
		json.put("name", "hoge");
		final RequestEntity<Map<String, String>> request = RequestEntity.post(URI.create("/"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(json);
		testRestTemplate.exchange(request, Void.class);

		Message<byte[]> message = output.receive();
		assertEquals("{\"name\":\"hoge\"}", new String(message.getPayload()));
	}
}
